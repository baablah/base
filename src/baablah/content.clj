(ns baablah.content
  (:require
    [baablah.digest :as digest]
    [hiccup.core :as h]))


;; Current database view
(def db (atom {:users {1 :ray                               ;; More fields but you get it.
                       2 :annette
                       3 :lil}
               :content {}}))

;; In memory database history for playing around
(def db-history (atom @db))

(defn dereference
  [{:keys [user digest]}]
  (some->> (get-in @db-history [:content user])
           (filter #(= digest (:digest %)))
           first
           :digest))

(defn link-post
  [user digest]
  (when-let [valid-digest (dereference {:user user :digest digest})]
    {:link {:user user :digest valid-digest}}))

(defn update-db
  [user new-post]
  ;; TODO ... squash duplicates
  (swap! db assoc-in [:content user] new-post)
  (swap! db-history update-in [:content user] (comp vec conj) new-post)
  (get-in @db [:content user]))

(defn create-post
  [user {:keys [digest] :as post}]
  (update-db user {:post post :digest digest}))

(defn clone-post
  [user other-user digest]
  (when-let [post (link-post other-user digest)]
    (update-db user post)))

(defn- add-greeting-html
  [{:keys [hiccup] :as greeting}]
  (assoc greeting :html (h/html hiccup)))

(defn default-greet
  [text]
  {:hiccup [:div [:p text]]
   :text text
   :digest (digest/edn->hex-digest text)})

(def greeting
  (add-greeting-html
    (default-greet "Hello World")))

(def greeting-be
  (add-greeting-html
    (default-greet "Dag Loonbeek")))

(def greeting-boro
  (add-greeting-html
    (default-greet "Now then chorber")))

;; Yes, we have to deal with the nonsense
(def greeting-slur
  (add-greeting-html
    (default-greet "Hello Morons")))


(comment
  (create-post 1 greeting)
  (create-post 2 greeting-boro)
  (clone-post 3 2 "7BC47F3ABDB97A03D9572F17C8448DE5D8972506")
  )
