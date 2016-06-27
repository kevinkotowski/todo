(ns todo.persist)
  ;(:require [clojure.string :as string]))

(require '[redis-async.core :as redis-async])
(require '[redis-async.client :as client])
(require '[cheshire.core     :as cheshire])

(def redis (redis-async/make-pool {:hostname "localhost" :port 6379}))

(let [ c1 (client/set redis "TEST:0" (cheshire/encode {:name "Boo" :desc "Boogy" :done true}) )
       c2 (client/get redis "TEST:0")
       ;x1 (client/set redis "ID" -1)
      ]
  (def task (cheshire/decode (client/<!! c2) true))
  (println "name: >" (get task :name) "<" )
  (println "desc: >" (get task :desc) "<" )
  (println "done: >" (get task :done) "<" )
  )

(defn getId []
  (def asyncID (client/get redis "ID") )
  (if (some? asyncID)
    (client/incr redis "ID")
    (client/set redis "ID" 1) )
  (def newAsyncID (client/get redis "ID") )
  (Integer/parseInt (client/<!! newAsyncID))
  )

(defn countAll [todoset]
  (def asyncCount (client/scard redis todoset) )
  (client/<!! asyncCount)
  )

(defn create [todoset, desc]
  (client/sadd redis todoset (cheshire/encode
                                  {:id (getId) :done false :desc desc}) ) )

(defn getAll [todoset]
  (def asyncAll (client/smembers redis todoset) )
  ;(mapv cheshire/encode (client/<!! asyncAll))
  (mapv list (client/<!! asyncAll))
  )
