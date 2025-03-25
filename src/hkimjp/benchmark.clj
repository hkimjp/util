(ns hkimjp.benchmark
  (:require [criterium.core :refer [with-progress-reporting quick-bench]]))

#_(defmacro quick [expr]
    `(with-progress-reporting
       (quick-bench ~expr :verbose)))

(defmacro with-progress [expr]
  `(with-progress-reporting
     (quick-bench ~expr))) ; without :verbose

(defmacro quick [expr]
  `(quick-bench ~expr)) ; without progress reporting)

(let [time*
      (fn [^long duration-in-ms f]
        (let [^com.sun.management.ThreadMXBean bean (java.lang.management.ManagementFactory/getThreadMXBean)
              bytes-before (.getCurrentThreadAllocatedBytes bean)
              duration (* duration-in-ms 1000000)
              start (System/nanoTime)
              first-res (f)
              delta (- (System/nanoTime) start)
              deadline (+ start duration)
              tight-iters (max (quot (quot duration delta) 10) 1)]
          (loop [i 1]
            (let [now (System/nanoTime)]
              (if (< now deadline)
                (do (dotimes [_ tight-iters] (f))
                    (recur (+ i tight-iters)))
                (let [i' (double i)
                      bytes-after (.getCurrentThreadAllocatedBytes bean)
                      t (/ (- now start) i')]
                  (println
                   (format "Time per call: %s   Alloc per call: %,.0fb   Iterations: %d"
                           (cond (< t 1e3) (format "%.0f ns" t)
                                 (< t 1e6) (format "%.2f us" (/ t 1e3))
                                 (< t 1e9) (format "%.2f ms" (/ t 1e6))
                                 :else (format "%.2f s" (/ t 1e9)))
                           (/ (- bytes-after bytes-before) i')
                           i))
                  first-res))))))]
  (defmacro time+
    "Like `time`, but runs the supplied body for 2000 ms and prints the average
  time for a single iteration. Custom total time in milliseconds can be provided
  as the first argument. Returns the returned value of the FIRST iteration."
    [?duration-in-ms & body]
    (let [[duration body] (if (integer? ?duration-in-ms)
                            [?duration-in-ms body]
                            [2000 (cons ?duration-in-ms body)])]
      `(~time* ~duration (fn [] ~@body)))))

(defn heap []
  (let [u (.getHeapMemoryUsage (java.lang.management.ManagementFactory/getMemoryMXBean))
        used (/ (.getUsed u) 1e6)
        total (/ (.getMax u) 1e6)]
    (format "Used: %.0f/%.0f MB (%.0f%%), free: %.0f MB" used total (/ used total 0.01)
            (/ (.freeMemory (Runtime/getRuntime)) 1e6))))

; what is this?
;
; (defmacro debugging-tools []
;   '(do
;      (require '[clj-async-profiler.core :as prof])
;      (require '[clj-java-decompiler.core :refer [decompile]])
;      (require '[clj-memory-meter.core :as mm])

;      (.refer *ns* 'time+ #'user/time+)
;      (.refer *ns* 'heap #'user/heap)))

; (comment
;   (debugging-tools)
;   ; Execution error (FileNotFoundException) at user/eval11131 (REPL:55).
;   ; Could not locate clj_async_profiler/core__init.class, clj_async_profiler/core.clj or clj_async_profiler/core.cljc on classpath. Please check that namespaces with dashes use underscores in the Clojure file name.
;   :rcf)

; tarai
(defn tarai
  [x y z]
  (if (<= x y)
    y
    (tarai (tarai (dec x) y z)
           (tarai (dec y) z x)
           (tarai (dec z) x y))))

(defn tarai-memo [x y z]
  (let [memo (atom {})]
    (letfn [(tarai [x y z]
              (or (get @memo [x y z])
                  (if (<= x y)
                    y
                    (let [result (tarai (tarai (- x 1) y z)
                                        (tarai (- y 1) z x)
                                        (tarai (- z 1) x y))]
                      (swap! memo assoc [x y z] result)
                      result))))]
      (tarai x y z))))

(defn tarai-lazy [x y z]
  (letfn [(tarai [fx fy fz]
            (if (<= (fx) (fy))
              (fy)
              (tarai (fn [] (tarai (fn [] (- (fx) 1)) fy fz))
                     (fn [] (tarai (fn [] (- (fy) 1)) fz fx))
                     (fn [] (tarai (fn [] (- (fz) 1)) fx fy)))))]
    (tarai (fn [] x) (fn [] y) (fn [] z))))
