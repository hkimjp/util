(ns user
  (:require
   [clojure.java.io :as io]
   [hkimjp.benchmark :as b]
   [hkimjp.datascript :as d]
   [hkimjp.util :as u]))

(comment
  (u/hello "github")
  (take 10 u/primes)

  (time (b/tarai 10 5 3))
  (b/time+ (b/tarai 10 5 3))
  (b/quick (b/tarai 10 5 3))

  (d/start "storage/db.sqlite")

  (d/conn?)

  (d/puts [{:db/add -1, :name "hiroshi", :age 18, :like "clojure"}])

  (d/puts [{:db/id -1, :name "kimura"}
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
