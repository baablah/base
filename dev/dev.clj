(ns dev
  (:require
    [baablah.http :as http]
    [clojure.tools.namespace.repl :as repl])
  (:import (org.eclipse.jetty.server Server)))

(println "Use (go) to start the system.")

(repl/set-refresh-dirs "dev" "src")

(defonce http-server (atom nil))

(defn go []
  (reset! http-server (http/start-jetty)))

(defn reset []
  (when-let [server @http-server]
    (println "Stopping server")
    (.stop ^Server server))
  (repl/refresh :after 'dev/go))

