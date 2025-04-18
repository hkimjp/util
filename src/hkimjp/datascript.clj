(ns hkimjp.datascript
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [datascript.core :as d]
   [datascript.storage.sql.core :as storage-sql]
   [taoensso.telemere :as t]))

(defonce storage (atom nil))

(def conn nil)

(defn shorten
  ([s] (shorten s 40))
  ([s n] (let [pat (re-pattern (str "(^.{" n "}).*"))]
           (str/replace-first s pat "$1..."))))

(defn make-storage [db]
  (t/log! :info (str "create pooled-datasource " db))
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

(defn create! [db]
  (t/log! :info "create sqlite3 backended datascript.")
  (reset! storage (make-storage db))
  (def conn (d/create-conn nil {:storage @storage})))

(defn restore
  ([] (restore "target/db.sqlite"))
  ([db]
   (t/log! {:level :info :db db} "restore")
   (reset! storage (make-storage db))
   (def conn (d/restore-conn @storage))))

(defn start
  ([] (start "target/db.sqlite"))
  ([db]
   (t/log! {:level :info :db db} "start on-memory datascript.")
   (if (.exists (io/file db))
     (restore db)
     (create! db))))

(defn stop []
  (t/log! :info "stop")
  (storage-sql/close @storage)
  (def conn nil))

(comment
  (create! "target/db.sqlite")
  (start)
  (restore)
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

