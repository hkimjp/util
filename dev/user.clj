(ns user
  (:require [hkimjp.benchmark :as b]
            [hkimjp.datascript :as d]
            [hkimjp.util :as u]))
(comment
  (u/hello "github")
  (take 10 u/primes)

  (time (b/tarai 10 5 3))
  (b/time+ (b/tarai 10 5 3))
  (b/quick (b/tarai 10 5 3))

  (d/start)
  (d/put [{:db/add -1 :name "hirosi"}
          {:db/add -1 :family "kimura"}
          {:db/add -1 :age 62}])
  (d/q '[:find ?e ?name ?family ?age
         :where
         [?e :name ?name]
         [?e :family ?family]
         [?e :age ?age]])

  (d/q '[:find ?e
         :where
         [?e]])

  (d/put [{:db/id -1 :name "hirosi"}
          {:db/id -1 :family "kimura"}
          {:db/id -1 :age 62}])

  (d/pull 4)
  (d/pull [:name] 4)
  :rcf)
