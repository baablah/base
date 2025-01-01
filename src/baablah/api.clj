(ns baablah.api
  (:require
    [baablah.content :as content]))

(defn user-content
  [user-id]
  (condp = user-id
    1 content/greeting-be
    2 content/greeting-boro
    3 content/greeting-slur
    content/greeting))
