(ns todo.persist
  )

(require '[redis-async.core :as redis-async])
(require '[redis-async.client :as client])
(require '[clojure.data.json :as json])

(def p (redis-async/make-pool {:hostname "localhost" :port 6379}))

(let [c1 (client/set p "X" "TEST")
      c2 (client/set p "Y" "TEST2")
      c3 (client/set p "Z" {:name "John" :age 30})
      c4 (client/get p "X")
      c5 (client/get p "Y")
      c6 (client/get p "Z")
      c7 (client/get p "Z")
      ]
  (println (client/<!! c4))
  (println (client/<!! c5))
  ;(println (:name c7))
  (println "redis: " (client/<!! c7))
  ;(println "json: " (json/write-str (sorted-map  '(client/<!! c6) )))

  (println "json:  " (json/write-str (client/<!! c6)) )
  ;(println "json:  " (json/read-str (client/<!! c6) :key-fn keyword ) )

  ;(println "name:  " c6)
  ;(println (:name (hash-map (client/<!! c6)) )  )
  ;(println (hash-map ("key"  client/<!! c6) )  )
  ;(println
  ;  (into {}
  ;        (for [[k v] (client/<!! c6)]
  ;          [k v]))
  ;  )
  )

