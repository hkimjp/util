(ns hkimjp.carmine
  (:refer-clojure :exclude [set get keys])
  (:require [taoensso.carmine :as car]))

(defonce my-conn-pool (car/connection-pool {}))
(def     my-conn-spec {:uri "redis://localhost:6379"})
(def     my-wcar-opts {:pool my-conn-pool, :spec my-conn-spec})

(defmacro wcar* [& body] `(car/wcar my-wcar-opts ~@body))

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

(comment
  ; carmine

  (defn recents [key]
    (let [keys (keys key)]
      (->> (map (fn [k] [k (get k)]) keys)
           (sort-by second)
           reverse)))

  (doseq [n (range 1, 10)]
    (setex (str "foo:" n) (* 2 n) n))

  (recents "foo:*")

  :rcf)
(comment

  (defn now []
    (str (java.util.Date.)))

  (setex "kp:hkimura" 10 (now))
  (get "kp:hkimura")
  (keys "kp:*")

  (wcar*
   (car/ping)
   (car/set "foo" "bar")
   (car/get "foo")) ; => ["PONG" "OK" "bar"] (3 commands -> 3 replies)

  (wcar*
   (car/keys "foo"))

  (wcar* (car/zadd "z" "GT" 235 "hkimura"))

  :rcf)
