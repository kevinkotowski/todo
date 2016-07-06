(ns api.persist)

(require '[redis-async.core :as redis-async])
(require '[redis-async.client :as client])

(def redis (redis-async/make-pool {:hostname "localhost" :port 6379}))

(defn getId []
  (def asyncID (client/get redis "ID") )
  (if (some? asyncID)
    (client/incr redis "ID")
    (client/set redis "ID" 1) )
  (def newAsyncID (client/get redis "ID") )
  (Integer/parseInt (client/<!! newAsyncID) )
  )

(defn countAll [db, entity]
  (def query (str db ":" entity "*") )
  (def asyncKeys (client/keys redis query) )
  (def keyVec (client/<!! asyncKeys) )
  (if (= nil (count keyVec))
    0
    (count keyVec)
    )
  )

(defn createOne [db, entity, valueMap]
  ;(println ">>>persist.createOne: " valueMap)
  ;(println ">>>persist.createOne: " (:desc valueMap) )
  (def compositeId (str db ":" entity ":" (getId) ) )
  (client/hmset redis compositeId
                "done" (if (= nil (:done valueMap) ) "0" (:done valueMap) )
                "desc" (:desc valueMap) )
  (println ">>>persist.createOne compositeId: " compositeId)
  (identity compositeId)
  )

(defn getOne [db, entity, id]
  ;(println ">>>persist.getOne id: " id)
  (def asyncGetDone (client/hget redis id "done") )
  (def asyncGetDesc (client/hget redis id "desc") )
  (def done (client/<!! asyncGetDone) )
  (def desc (client/<!! asyncGetDesc) )
  ;(println ">>>persist.getOne done: " done)
  ;(println ">>>persist.getOne desc: " desc)
  (if (and (empty? done) (empty? desc))
    ;(def response {:done nil :desc nil} )
    (def response nil)
    (def response (hash-map :id (str id) :done (str done) :desc (str desc) ) )
    )
  ;(println "...getOne" id response)
  ;(assoc response :id id)
  (identity response)
  )

(defn getAllIds [db, entity]
  (def query (str db ":" entity "*") )
  (def asyncKeys (client/keys redis query) )
  (client/<!! asyncKeys)
  )

(defn getAll [db, entity]
  (def query (str db ":" entity "*") )
  (def asyncKeys (client/keys redis query) )
  (let [keys (client/<!! asyncKeys) ]
    (mapv #(getOne db entity %) keys)
    )
  )

(defn updateOne [db, entity, id, valueMap]
  ;(println "...updateOne" id valueMap)
  (client/hmset redis id
                "done" (if (= nil (:done valueMap) ) "0" (:done valueMap) )
                "desc" (:desc valueMap))
  )

(defn deleteOne [db, entity, id]
  ;(println "...deleteOne:" id)
  (client/hdel redis id "done" "desc" )
  )

(defn deleteAll [db, entity]
  (def all (getAllIds db entity) )
  (mapv #(deleteOne db, entity, %)  all)
  )