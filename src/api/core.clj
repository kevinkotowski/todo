(ns api.core
  (:require [api.persist :as persist]
            [clojure.string :as string])
  (:use [clojure.string :only (join split)])
  (:refer-clojure :exclude [key, read, update])
  )

(defn pairToHash [pair]
  (def splitPair (split pair #"="))
  ;(println "splitPair: " splitPair)
  (def key (keyword (first splitPair) ) )
  (if (= 2 (count splitPair) )
    ;(hash-map key (str "\"" (string/replace (nth splitPair 1) "+" " ") "\""))
    (hash-map key (string/replace (nth splitPair 1) "+" " ") )
    (hash-map key nil)
    )
  )

(defn toSafeVal [unsafeVal]
  (if (empty? unsafeVal) "" (string/replace unsafeVal " " "+") ) )

(defn convert [urlSafe]
  ;(println "...api.convert urlSafe: " urlSafe)
  (def htmlVector (split urlSafe #"&") )
  ;(println "...split html: " htmlVector)
  (def newHash (ref {}) )
  (mapv #(dosync (ref-set newHash (merge @newHash (pairToHash %) ) ) ) htmlVector)
  ;(println "...html hash: " @newHash)
  (identity @newHash)
  )

(defn revert [oldMap]
  (def newString
    (reduce-kv (fn [result key val]
                 (if (empty? val)
                   (str result (name key) "=&")
                   (str result (name key) "=" (toSafeVal (name (keyword val))) "&" )
                   ))
               "" oldMap) )
  ; removes the final character, which is always a "&"
  (subs newString 0 (- (.length newString) 1) )
  )


(defn create [db, entity, data]
  (println "...api.create " data)
  (persist/createOne db entity (convert data))
  )

(defn read [db, entity, id]
  (def fromDb (persist/getOne db entity id) )
  (def response (if (empty? fromDb) nil (revert fromDb) ) )
  (println "...api.read " id response)
  (identity response)
  )

(defn update [db, entity, id, data]
  (println "...api.update " data)
  (persist/updateOne db entity id (convert data))
  )

(defn delete [db, entity, id]
  (println "...api.delete " id)
  (persist/deleteOne db entity id)
  )

(defn countAll [db, entity]
  (persist/countAll db entity)
  )

(defn readAll [db, entity]
  (persist/getAll db entity)
  )

(defn deleteAll [db, entity]
  (persist/deleteAll db, entity)
  )
