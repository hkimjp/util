(ns user
  (:require
   [clojure.java.io :as io]
   [hkimjp.benchmark :as b]
   [hkimjp.carmine :as c]
   [hkimjp.datascript :as d]
   [hkimjp.util :as u]))

(comment
  ; carmine

  (defn sorted [key]
    (let [keys (c/keys key)]
      (->> (map (fn [k] [k (c/get k)]) keys)
           (sort-by second)
           reverse)))

  (doseq [n (range 1, 10)]
    (c/setex (str "foo:" n) (* 2 n) n))

  (sorted "foo:*")

  :rcf)

(comment
  ; datascript

  (d/start "storage/db.sqlite")

  (d/conn?)

  (d/put! [{:db/add -1, :name "hiroshi", :age 18, :like "clojure"}])
  (d/put! [{:db/id -1, :name "kimura"}
           {:db/id -1, :database "DataScript"}])

  (d/q '[:find ?name ?age ?like
         :where
         [?e :name ?name]
         [?e :age ?age]
         [?e :like ?like]])

  (d/q '[:find ?name ?database
         :where
         [?e :name ?name]
         [?e :database ?database]])

  (d/gc)

  (d/stop)

  (d/start (io/file "storage/db.sqlite"))

  (d/conn?)

  (d/q '[:find ?e ?name ?age ?like
         :where
         [?e :name ?name]
         [?e :age ?age]
         [?e :like ?like]])

  (d/q '[:find ?name ?database
         :where
         [?e :name ?name]
         [?e :database ?database]])

  (d/pull 3)

  (d/stop)

  :rcf)
