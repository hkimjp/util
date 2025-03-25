(ns hkimjp.util
  (:require [clojure.string :as str]
            [clojure.math :as math]
            [clojure.math.combinatorics :as combo]))

(defn- probe
  [msg any]
  (prn msg any)
  any)

;; primes
;; Excerpted from "Programming Clojure, Third Edition",
;; published by The Pragmatic Bookshelf.
;; Copyrights apply to this code. It may not be used to create training material,
;; courses, books, articles, and the like. Contact us if you are in doubt.
;; We make no guarantees that this code is fit for any purpose.
;; Visit http://www.pragmaticprogrammer.com/titles/shcloj3 for more book information.
(def primes
  (concat
   [2 3 5 7]
   (lazy-seq
    (let [primes-from
          (fn primes-from [n [f & r]]
            (if (some #(zero? (rem n %))
                      (take-while #(<= (* % %) n) primes))
              (recur (+ n f) r)
              (lazy-seq (cons n (primes-from (+ n f) r)))))
          wheel (cycle [2 4 2 4 6 2 6 4 2 4 6 6 2 6  4  2
                        6 4 6 8 4 2 4 2 4 8 6 4 6 2  4  6
                        2 6 6 4 2 4 6 2 6 4 2 4 2 10 2 10])]
      (primes-from 11 wheel)))))

; power
; (pow a b) returns double.
; (power n m) returns int.
(defn sq [x] (* x x))

(defn power [base n]
  (cond
    (zero? n) 1
    (even? n) (sq (power base (quot n 2)))
    :else (* base (power base (dec n)))))

; factor integer
(defn- div-multi-by-2 [n ret]
  (if (zero? (rem n 2))
    (recur (quot n 2) (conj ret 2))
    [n ret]))

(defn- fi-aux [n d ret]
  (cond
    (< n (* d d)) (if (= n 1)
                    ret
                    (conj ret n))
    (zero? (rem n d)) (recur (quot n d) d (conj ret d))
    :else (recur n (inc (inc d)) ret)))

(defn factor-integer [n]
  (if (< n 2)
    [n]
    (let [[m ret] (div-multi-by-2 n [])]
      (fi-aux m 3 ret))))

; prime?
(defn- prime?-aux [n d]
  (cond
    (< n (* d d)) true
    (zero? (rem n d)) false
    :else (recur n (inc (inc d)))))

(defn prime? [n]
  (cond
    (< n 3) (= n 2)
    (even? n) false
    :else (let [[n _] (div-multi-by-2 n [])]
            (prime?-aux n 3))))

(defn- prime'-aux [n i]
  (or (zero? (rem n i))
      (zero? (rem n (+ i 2)))))

(defn prime'
  "need improve.
   take twice time than `prime?`. maybe every? is slow?"
  [n]
  (cond
    (< n 6) (or (= n 2) (= n 3) (= n 5))
    (zero? (rem n 2)) false
    (zero? (rem n 3)) false
    :else (every? false?
                  (map #(prime'-aux n %) (range 5 (+ (math/sqrt n) 1) 6)))))

; next-prime
(defn next-prime
  "return the smallest prime number larger than `n`."
  [n]
  (-> (drop-while (complement prime?) (iterate inc (+ 1 n)))
      first))
(comment
  (= 119 (next-prime 117)))

; prime-pi
(defn prime-pi
  "number of primes less than or equal to x."
  [n]
  (-> (take-while #(< % n) primes)
      count))

; cartesian product
; combo/cartesian-product
(defn cart2 [xs ys]
  (for [x xs y ys]
    (cons x y)))

(defn- cart-aux [xss ret]
  (if (empty? xss)
    ret
    (cart-aux (butlast xss) (cart2 (last xss) ret))))

(defn cart [xss]
  (cart-aux xss [[]]))

(comment
  (cart [[1 2] [:a :b]]))

; divisors
; oridinaly definition
(defn divisors'
  "use divisors, which is 5 times faster than this."
  [n]
  (let [d1 (filter #(zero? (rem n %)) (range 1 (+ 1 (math/sqrt n))))
        d2 (map #(quot n %) (reverse d1))]
    (if (= (last d1) (first d2))
      (concat d1 (rest d2))
      (concat d1 d2))))

(defn- factor-expand
  "(2 2 2) => (1 2 4 8)
   (3) => (1 3)"
  [coll]
  (map #(power (first coll) %) (range (inc (count coll)))))

(defn divisors [n]
  (->> (factor-integer n)
       (partition-by identity)
       (map factor-expand)
       (apply combo/cartesian-product)
       (map #(reduce * %))))

; fold
; https://stackoverflow.com/questions/16800255/how-do-we-do-both-left-and-right-folds-in-clojure
(defn fold-l
  [f val coll]
  (if (empty? coll)
    val
    (fold-l f (f val (first coll)) (rest coll))))

(defn fold-r
  [f val coll]
  (if (empty? coll)
    val
    (f (fold-r f val (rest coll)) (first coll))))

(comment
  (fold-l + 0 (range 10))
  (fold-r * 1 (range 1 10)))

; flatten-all
(defn flatten-all [coll]
  (if (sequential? coll)
    (if (empty? coll)
      []
      (concat (flatten-all (first coll))
              (flatten-all (rest coll))))
    [coll]))

(comment
  (flatten-all [])
  (flatten-all [1 2 3])
  (flatten-all [[1] [2] [3]])
  (flatten-all [[1 2] [3 [4] [[5]]]])
  :rcf)

; reverse-all
; sequential? is the key.
(defn reverse-all [coll]
  (if (sequential? coll)
    (if (empty? coll)
      []
      (conj (reverse-all (rest coll))
            (reverse-all (first coll))))
    coll))

(comment
  (reverse-all [])
  (reverse-all [1 2 3])
  (reverse-all [1 [[2 3] 4 [5 [[6]]] 7] 8 9])
  :rcf)

;; shorten string
(defn shorten
  ([s] (shorten s 20))
  ([s n] (let [pat (re-pattern (str "(^.{" n "}).*"))]
           (str/replace-first s pat "$1..."))))

(comment
  (shorten (str (range 100)))
  (shorten (vec (range 100)) 10)
  :rcf)

; binary-search
(defn binary-search
  "Return the index if cound or nil."
  [v value]
  (loop [low 0 high (dec (count v))]
    (if (<= high (inc low))
      (cond (= value (v low)) low
            (= value (v high)) high
            :else nil)
      (let [middle (quot (+ low high) 2)]
        (if (< (v middle) value)
          (recur (inc middle) high)
          (recur low middle))))))

(comment
  (binary-search (vec (range 1000)) 500)
  (let [v (vec (range 0 100000 2))]
    (time (binary-search v 50000)) ; => 0.06ms
    (time (.indexOf v 50000)) ; => 1.35ms
    (time (binary-search v 50001)) ; => 0.04ms
    (time (.indexOf v 50001)) ; => 2.14ms
    ))
