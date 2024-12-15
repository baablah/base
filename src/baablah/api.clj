(ns baablah.api
  (:require
    [baablah.content :as content]
    [clojure.spec.alpha :as s]
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
    [spec-tools.spell :as spell])
  (:import (org.eclipse.jetty.server Server)))

(s/def ::user-id int?)
(s/def ::greeting string?)
(s/def ::path-params (s/keys :req-un [::user-id]))

(defn user-content
  [user-id]
  {:status 200
   :body (condp = user-id
           1 content/greeting-be
           2 content/greeting-boro
           3 content/greeting-slur
           content/greeting)})

(def app
  (ring/ring-handler
    (ring/router
      ["/api"
       ["/users/:user-id" {:get {:parameters {:path ::path-params}
                                 :responses {200 {:body ::greeting}}
                                 :handler (fn [{{{:keys [user-id]} :path} :parameters :as x}]
                                            (user-content user-id))}}]]
      {;;:reitit.middleware/transform dev/print-request-diffs ;; pretty diffs
       :validate spec/validate                              ;; enable spec validation for route data
       :reitit.spec/wrap spell/closed ;; strict top-level validation
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

(def jetty (atom nil))

(defn start-jetty []
  (reset! jetty (jetty/run-jetty app {:port 3000
                                      :join? false})))

(defn stop-jetty []
  (when @jetty (.stop ^Server @jetty)))
