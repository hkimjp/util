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

  (d/stop)
  :rcf)
