(ns dev
  (:require
    [baablah.api :as api]
    [clojure.tools.namespace.repl :as repl])
  (:import (org.eclipse.jetty.server Server)))

(repl/set-refresh-dirs "dev" "src")

(defonce http-server (atom nil))

(defn go []
  (reset! http-server (api/start-jetty)))

(defn reset []
  (when-let [server @http-server]
    (println "Stopping server")
    (.stop ^Server server))
  (repl/refresh :after 'dev/go))
