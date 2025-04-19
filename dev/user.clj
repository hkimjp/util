(ns user
  (:require [hkimjp.benchmark :as b]
            [hkimjp.datascript :as d]
            [hkimjp.util :as u]))

(comment
  (def c (hc/build-http-client {:connect-timeout 10000
                                :redirect-policy :always}))

  (u/hello "github")
  (take 10 u/primes)

  (time (b/tarai 10 5 3))
  (b/time+ (b/tarai 10 5 3))
  (b/quick (b/tarai 10 5 3))

  (d/start "target/db.sqlite")
  (d/conn?)

  (d/puts [{:db/add -1, :name "hiroshi", :age 63, :like "clojure"}])

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

  (d/start "target/db.sqlite")
  (d/conn?)
  (d/stop)

  :rcf)
