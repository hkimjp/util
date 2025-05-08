(ns hkimjp.carmine
  (:require [taoensso.carmine :as car]))

(defonce my-conn-pool (car/connection-pool {}))
(def     my-conn-spec {:uri "redis://localhost:6379"})
(def     my-wcar-opts {:pool my-conn-pool, :spec my-conn-spec})

(defmacro wcar* [& body] `(car/wcar my-wcar-opts ~@body))

(comment
  (wcar*
   (car/ping)
   (car/set "foo" "bar")
   (car/get "foo")) ; => ["PONG" "OK" "bar"] (3 commands -> 3 replies)

  (wcar*
   (car/keys "foo"))
  :rcf)

(defn set [key value]
  (wcar* (car/set key value)))

(defn setex [key expire value]
  (wcar* (car/setex key expire value)))

(defn get [key]
  (wcar* (car/get key)))

(defn keys [key]
  (wcar* (car/keys key)))

(defn ttl [key]
  (wcar* (car/ttl key)))
