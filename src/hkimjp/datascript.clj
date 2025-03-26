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

(def db "target/db.sqlite")

(defn make-storage [db]
  (let [datasource (doto (org.sqlite.SQLiteDataSource.)
                     (.setUrl (str "jdbc:sqlite:" db)))
        pooled-datasource (storage-sql/pool
                           datasource
                           {:max-conn 10
                            :max-idle-conn 4})]
    (storage-sql/make pooled-datasource {:dbtype :sqlite})))

; even using on-memory database, create useless storage.
; is this bad?
; (def storage (make-storage db))

(def conn nil)

(defn conn? []
  (d/conn? conn))

(defn start []
  (t/log! :info "start on-memory datascript.")
  (def conn (d/create-conn)))

(defn create [db]
  (t/log! :info "create sqlite3 backended datascript.")
  (let [storage (make-storage db)]
    (def conn (d/create-conn nil {:storage storage}))))

(defn restore [db]
  (t/log! :info "restore")
  (let [storage (make-storage db)]
    (def conn (d/restore-conn storage))))

(defn stop []
  (t/log! :info "stop")
  ; safe?
  ; (storage-sql/close storage)
  (def conn nil))

(defmacro q [query & inputs]
  (t/log! :info (str "q " (shorten query)))
  `(d/q ~query @conn ~@inputs))

(defn put [facts]
  (t/log! :info (str "put " (shorten facts)))
  (d/transact! conn facts))

(defn pull
  ([eid] (pull ['*] eid))
  ([selector eid] (t/log! :info (str "pull " selector " " eid))
                  (d/pull @conn selector eid)))

