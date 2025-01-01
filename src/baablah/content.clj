(ns baablah.content
  (:require
    [baablah.digest :as digest]))

(defn ->content-with-hiccup+digest
  [text]
  {:hiccup [:div [:p text]]
   :text text
   :digest (digest/edn->hex-digest text)})

(def greeting
  (->content-with-hiccup+digest "Hello World"))

(def greeting-be
  (->content-with-hiccup+digest "Dag Loonbeek"))

(def greeting-boro
  (->content-with-hiccup+digest "Now then chorber"))

;; Yes, we have to deal with the nonsense
(def greeting-slur
  (->content-with-hiccup+digest "Hello Morons"))
