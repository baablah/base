(ns user)

(println "Welcome to the development REPL. Use (dev) for getting started.")

(defn dev
  []
  (require 'dev)
  (in-ns 'dev)
  (println "You can use (reset) to reload the code and restart the system."))


(defn go
  []
  (println "Don't you mean (dev) ?"))