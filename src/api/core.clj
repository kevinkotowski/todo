(ns api.core
  (:require [api.persist :as persist]
            [clojure.string :as string])
  ;(:use [clojure.string :only (join split)])
  (:refer-clojure :exclude [key, read, update])
  )

(defn toSafeVal [unsafeVal]
  (if (empty? unsafeVal) "" (string/replace unsafeVal " " "+") ) )

(defn convert [params]
    ;(println "...api.core.convert params: " params)
    (let [{:strs [done desc] } params]
      (hash-map :done done :desc desc))
  )

;(defn revert [results]
;  (println "...api.core.revert params:    " results)
;  (println "...api.core.revert hardcoded: " {:desc "hard in api.core.revert"})
;  )

(defn revertUrlSafe [oldMap]
  (println "api.revert oldMap: " oldMap)
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


(defn create [db, entity, params]
  ;(def convertedData (convert data))
  ;(persist/createOne db entity convertedData)
  (persist/createOne db entity (convert params) )
  )

(defn read [db, entity, id]
  ;(persist/getOne db entity id)
  (def fromDb (persist/getOne db entity id) )
  (def response (if (empty? fromDb) nil (revertUrlSafe fromDb) ) )
  ;(def response (if (empty? fromDb) nil (revert fromDb) ) )
  (println "...api.core.read " id response)
  (identity response)
  )

(defn update [db, entity, id, data]
  ;(println "...api.update " data)
  (persist/updateOne db entity id (convert data))
  )

(defn delete [db, entity, id]
  ;(println "...api.delete " id)
  (persist/deleteOne db entity id)
  )

(defn countAll [db, entity]
  (persist/countAll db entity)
  )

(defn readAll [db, entity]
  (persist/getAllIds db entity)
  )

(defn deleteAll [db, entity]
  (persist/deleteAll db, entity)
  )
