(ns baablah.db)

;; Current database view
(def db (atom {:users {1 :ray                               ;; More fields needed but you get it.
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

(defn update-db
  [user new-post]
  ;; TODO ... squash duplicates
  (swap! db assoc-in [:content user] new-post)
  (swap! db-history update-in [:content user] (comp vec conj) new-post)
  (get-in @db [:content user]))

(defn create-post
  [user {:keys [digest] :as post}]
  (update-db user {:post post :digest digest}))

(defn link-post
  [user digest]
  (when-let [valid-digest (dereference {:user user :digest digest})]
    {:link {:user user :digest valid-digest}}))

(defn clone-post
  [user other-user digest]
  (when-let [post (link-post other-user digest)]
    (update-db user post)))


(comment
  (use '[baablah.content])
  (create-post 1 greeting)
  (create-post 2 greeting-boro)
  (create-post 2 greeting-be)
  (clone-post 3 2 (:digest greeting-be))
  @db
  @db-history
  )
