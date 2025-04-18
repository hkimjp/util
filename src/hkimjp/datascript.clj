(ns hkimjp.datascript
  (:require
   [clojure.string :as str]
   [datascript.core :as d]
   [datascript.storage.sql.core :as storage-sql]
   [taoensso.telemere :as t]))

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

(def storage (atom nil))

; FIXME: inline def
(def conn nil)

(defn conn? []
  (d/conn? conn))

(defn start []
  (t/log! :info "start on-memory datascript.")
  (def conn (d/create-conn)))

(defn create
  ([] (create "target/db.sqlite"))
  ([db]
   (t/log! :info "create sqlite3 backended datascript.")
   (reset! storage (make-storage db))
   (def conn (d/create-conn nil {:storage @storage}))))

(defn restore
  ([] (restore "target/db.sqlite"))
  ([db]
   (t/log! :info "restore")
   (reset! storage (make-storage db))
   (def conn (d/restore-conn @storage))))

(defn stop []
  (t/log! :info "stop")
  (storage-sql/close @storage)
  (def conn nil))

(defmacro q [query & inputs]
  (t/log! :info (str "q " (shorten query)))
  `(d/q ~query @conn ~@inputs))

(defn put [fact]
  (t/log! :info (str "put " (shorten facts)))
  (d/transact! conn [facts]))

(defn puts [facts]
  (t/log! :info (str "put " (shorten facts)))
  (d/transact! conn facts))

(defn pull
  ([eid] (pull ['*] eid))
  ([selector eid]
   (t/log! :info (str "pull " selector " " eid))
   (d/pull @conn selector eid)))

