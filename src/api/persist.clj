(ns api.persist
  (:require [redis-async.core :as redis-async]
            [redis-async.client :as client]) )

(def redis (redis-async/make-pool {:hostname "localhost" :port 6379}) )

(defn getId []
  (let [ asyncID (client/get redis "ID") ]
    (if (some? asyncID)
      (client/incr redis "ID")
      (client/set redis "ID" 1) )
    (let [ newAsyncID (client/get redis "ID") ]
      (Integer/parseInt (client/<!! newAsyncID) ) ) ) )

(defn countAll [db, entity]
  (let [ query (str db ":" entity "*")
         asyncKeys (client/keys redis query)
         keyVec (client/<!! asyncKeys) ]
    (if (= nil (count keyVec))
      0
      (count keyVec) ) ) )

(defn createOne [db, entity, valueMap]
  (let [ compositeId (str db ":" entity ":" (getId) ) ]
    (client/hmset redis compositeId
                  "done" (if (= nil (:done valueMap) ) "0" (:done valueMap) )
                  "desc" (:desc valueMap) )
    (identity compositeId) ) )

(defn getOne [db, entity, id]
  ; db and entity are left unvalidated until requirements can be decided
  (let [ asyncGetDone (client/hget redis id "done")
         asyncGetDesc (client/hget redis id "desc")
         done (client/<!! asyncGetDone)
         desc (client/<!! asyncGetDesc) ]
    (if (and (empty? done) (empty? desc))
      (def response nil)
      (def response (hash-map :id (str id) :done (str done) :desc (str desc) ) ) )
    (identity response) ) )

(defn getAllIds [db, entity]
  (let [ query (str db ":" entity "*")
         asyncKeys (client/keys redis query) ]
    (client/<!! asyncKeys) ) )

(defn getAll [db, entity]
  (let [ query (str db ":" entity "*")
         asyncKeys (client/keys redis query) ]
    (let [keys (client/<!! asyncKeys) ]
      (mapv #(getOne db entity %) keys) ) )
  )

(defn updateOne [db, entity, id, valueMap]
  (client/hmset redis id
                "done" (if (= nil (:done valueMap) ) "0" (:done valueMap) )
                "desc" (:desc valueMap))
  )

(defn deleteOne [db, entity, id]
  (client/hdel redis id "done" "desc" )
  )

(defn deleteAll [db, entity]
  (let [ all (getAllIds db entity) ]
    (mapv #(deleteOne db, entity, %)  all) ) )