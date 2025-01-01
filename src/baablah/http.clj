(ns baablah.http
  (:require
    [baablah.api :as api]
    [clojure.spec.alpha :as s]
    [hiccup.core :as h]
    [reitit.coercion.spec]
    [reitit.dev.pretty :as pretty]
    [reitit.ring :as ring]
    [reitit.ring.coercion :as coercion]
    [reitit.ring.middleware.exception :as exception]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.parameters :as parameters]
    [reitit.ring.spec :as spec]
    [reitit.swagger-ui :as swagger-ui]
    [ring.adapter.jetty :as jetty]
    [spec-tools.spell :as spell]))

(s/def ::user-id int?)
(s/def ::greeting string?)
(s/def ::path-params (s/keys :req-un [::user-id]))

(defn- hiccup->html
  [{:keys [hiccup] :as _content}]
  (h/html hiccup))

(defn user-content
  [user-id]
  {:status 200
   :body (-> user-id
             api/user-content
             hiccup->html)})

(def app
  (ring/ring-handler
    (ring/router
      ["/api"
       ["/users/:user-id" {:get {:parameters {:path ::path-params}
                                 :responses {200 {:body ::greeting}}
                                 :handler (fn [{{{:keys [user-id]} :path} :parameters}]
                                            (user-content user-id))}}]]
      {;;:reitit.middleware/transform dev/print-request-diffs ;; pretty diffs
       :validate spec/validate                              ;; enable spec validation for route data
       :reitit.spec/wrap spell/closed                       ;; strict top-level validation
       :exception pretty/exception
       :data {:coercion reitit.coercion.spec/coercion
              :middleware [;; query-params & form-params
                           parameters/parameters-middleware
                           ;; content-negotiation
                           muuntaja/format-negotiate-middleware
                           ;; encoding response body
                           muuntaja/format-response-middleware
                           ;; exception handling
                           exception/exception-middleware
                           ;; decoding request body
                           muuntaja/format-request-middleware
                           ;; coercing response body
                           coercion/coerce-response-middleware
                           ;; coercing request parameters
                           coercion/coerce-request-middleware]}})
    (ring/routes
      (swagger-ui/create-swagger-ui-handler
        {:path "/"
         :config {:validatorUrl nil
                  :urls [{:name "swagger" :url "swagger.json"}
                         {:name "openapi" :url "openapi.json"}]
                  :urls.primaryName "openapi"
                  :operationsSorter "alpha"}})
      (ring/create-default-handler))))

(defn start-jetty []
  (let [port 3000]
    (println (str "Starting server on port " port))
    (jetty/run-jetty app {:port port
                          :join? false})))

