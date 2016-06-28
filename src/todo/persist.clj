(ns todo.persist)

(require '[redis-async.core :as redis-async])
(require '[redis-async.client :as client])

(def redis (redis-async/make-pool {:hostname "localhost" :port 6379}))
(def entity "tasks")

(defn getId []
  (def asyncID (client/get redis "ID") )
  (if (some? asyncID)
    (client/incr redis "ID")
    (client/set redis "ID" 1) )
  (def newAsyncID (client/get redis "ID") )
  (Integer/parseInt (client/<!! newAsyncID) )
  )

(defn countAll [todoenv]
  (def query (str todoenv ":" entity "*") )
  (def asyncKeys (client/keys redis query) )
  (def keyVec (client/<!! asyncKeys) )
  (count keyVec)
  )

(defn createOne [todoenv, desc]
  (def compositeId (str todoenv ":" entity ":" (getId) ) )
  (client/hmset redis compositeId "done" 0 "desc" desc)
  (identity compositeId)
  )

(defn getAll [todoenv]
  (def query (str todoenv ":" entity "*") )
  (def asyncKeys (client/keys redis query) )
  (client/<!! asyncKeys)
  )

(defn getOne [id]
  (def asyncGetDone (client/hget redis id "done") )
  (def asyncGetDesc (client/hget redis id "desc") )
  (def done (client/<!! asyncGetDone) )
  (def desc (client/<!! asyncGetDesc) )
  (hash-map :done done :desc desc)
  )

(defn updateOne [id valueMap]
  (client/hmset redis id "done" (:done valueMap) "desc" (:desc valueMap))
  )

(defn deleteOne [id]
  (client/hdel redis id "done" "desc" )
  )

(defn deleteAll [todoenv]
  (def all (getAll todoenv) )
  (mapv deleteOne all)
  )