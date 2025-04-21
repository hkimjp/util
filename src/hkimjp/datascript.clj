(ns hkimjp.datascript
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [datascript.core :as d]
   [datascript.storage.sql.core :as storage-sql]
   [taoensso.telemere :as t]))

(defonce storage (atom nil))

(def conn nil)

(defn- shorten
  ([s] (shorten s 40))
  ([s n] (let [pat (re-pattern (str "(^.{" n "}).*"))]
           (str/replace-first s pat "$1..."))))

(defn- make-storage [db]
  (try
    (let [datasource (doto (org.sqlite.SQLiteDataSource.)
                       (.setUrl (str "jdbc:sqlite:" db)))
          pooled-datasource (storage-sql/pool
                             datasource
                             {:max-conn 10
                              :max-idle-conn 4})]
      (storage-sql/make pooled-datasource {:dbtype :sqlite}))
    (catch Exception e
      (t/log! :error (.getMessage e))
      (throw (Exception. "db dir does not exist.")))))

(defn conn? []
  (d/conn? conn))

(defn- create!
  ([]
   (t/log! :info "create! on-memory datascript.")
   (alter-var-root #'conn (constantly (d/create-conn nil))))
  ([db]
   (t/log! :info "create! sqlite backended datascript.")
   (reset! storage (make-storage db))
   (alter-var-root #'conn
                   (constantly (d/create-conn nil {:storage @storage})))))

(defn- restore
  ([db]
   (t/log! :info "restore")
   (reset! storage (make-storage db))
   (alter-var-root #'conn
                   (constantly (d/restore-conn @storage)))))

(defn start
  ([]
   (t/log! :info "start on-memory datascript.")
   (create!))
  ([db]
   (t/log! :info "start datascript with sqlite backend.")
   (if (.exists (io/file db))
     (restore db)
     (create! db))))

(defn stop []
  (t/log! :info "stop")
  (storage-sql/close @storage)
  (alter-var-root #'conn (constantly nil)))

(comment
  (start "storage/db.sqlite")
  (conn?)
  (stop)
  (start "storage/db.sqlite")
  (conn?)
  (stop)
  :rcf)

;; ----------------------------------------

(defmacro q [query & inputs]
  (t/log! :info (str "q " (shorten query)))
  `(d/q ~query @conn ~@inputs))

(defn put [fact]
  (t/log! :info (str "put " (shorten fact)))
  (d/transact! conn [fact]))

(defn puts [facts]
  (t/log! :info (str "put " (shorten facts)))
  (d/transact! conn facts))

(defn pull
  ([eid] (pull ['*] eid))
  ([selector eid]
   (t/log! :info (str "pull " selector " " eid))
   (d/pull @conn selector eid)))

(comment
  (start)
  (put [{:db/id 100 :fact "fact"}])
  (pull 100)
  (stop)
  :rcf)
