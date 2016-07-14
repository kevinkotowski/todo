(ns api.core
  (:require [api.persist :as persist]
            [clojure.string :as string])
  (:refer-clojure :exclude [key, read, update])
  )

(defn toSafeVal [unsafeVal]
  (if (empty? unsafeVal) "" (string/replace unsafeVal " " "+") ) )

(defn convert [params]
  (let [{:strs [done desc] } params]
      (hash-map :done done :desc desc) ) )

(defn revertUrlSafe [oldMap]
  (let [ newString (reduce-kv (fn [result key val]
                       (if (empty? val)
                         (str result (name key) "=&")
                         (str result (name key) "=" (toSafeVal (name (keyword val))) "&" )
                         ))
                     "" oldMap)
        ]
    (subs newString 0 (- (.length newString) 1) ) ) )

(defn create [db, entity, params]
  (persist/createOne db entity (convert params) )
  )

(defn read [db, entity, id]
  (let [ fromDb (persist/getOne db entity id) ]
    (if (empty? fromDb) nil (revertUrlSafe fromDb) ) ) )

(defn update [db, entity, id, data]
  (persist/updateOne db entity id (convert data))
  )

(defn delete [db, entity, id]
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
