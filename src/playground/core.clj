(ns playground.core
  (:use ring.middleware.reload)
  (:use ring.adapter.jetty)
  (:use ring.middleware.resource)
  (:use ring.middleware.content-type)
  (:use ring.middleware.not-modified)
  (:use clostache.parser))


(defn adds-one [x]
  (+ x 1))

(defn multiple-let [x]
  (let [y x
        y (+ y 10)]
    y))

(defn wrap-auth [handler]
  (fn [request]
    (let [response (handler request)
          response (assoc-in response [:headers  "Authorization"] "Basic: Kevin+")
          response (assoc-in response [:headers  "Boogy"] "Basic: Kevin+")
          response (assoc-in response [:headers  "Pragma"] "no-cache")
          ]
      response
      )))

(defn wrap-age [handler]
  (fn [request]
    ;(println request)
    (let [response (handler request)]
      (assoc-in response [:headers  "Age"] "777")
      )))

(defn wrap-body-replace [handler]
  (fn [request]
    ;(println (str "...wrap-body-replace found method: " (:request-method request)) )
    (let [response (handler request)]
      (assoc response :body "Body snatcher is scary!")
      )))

(defn wrap-post [handler]
  (fn [request]
    ;(println (str "...wrap-post found method: " (:request-method request)) )
    (if (true? (= (:request-method request) :post) )
      (let [response (handler request)]
        (assoc response :body "POST snatcher!") )
      (let [response (handler request)]
        (identity response) )
      )))

(defn wrap-template [handler]
  (fn [request]
    (if (true? (= (:request-method request) :put) )
      (let [response (handler request)]
        (assoc response :body
            (render-resource "templates/hello.mustache" {:name "Kevin"})
            ))
      (let [response (handler request)]
        (identity response) )
      )))

(defn handler [req]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (str "Kevin, you freak!") })

(def player
  (-> handler
      (wrap-body-replace)
      (wrap-post)
      (wrap-template)
      (wrap-age)
      (wrap-auth)
      (wrap-resource "public")
      (wrap-content-type)
      (wrap-not-modified)
      (wrap-reload) ))

(defn play []
  (run-jetty #'player {:port 54321 :join? false}))

(use '[clojure.tools.namespace.repl :only (refresh)])


(println "...starting redis play in playground.core")
  (require '[redis-async.core :as redis-async])
  (require '[redis-async.client :as client])
  (require '[cheshire.core     :as cheshire])

  (def redis (redis-async/make-pool {:hostname "localhost" :port 6379}))

  (let [c1 (client/set redis "X" "TEST")
        c2 (client/set redis "Y" "TEST2")
        c3 (client/set redis "Z" (cheshire/encode {:name "Boo" :desc "Task"}) )
        c4 (client/get redis "X")
        c5 (client/get redis "Y")
        c6 (client/get redis "Z")
        c7 (client/get redis "Z")
        ]
    (println (client/<!! c4))
    (println (client/<!! c5))
    (println "name: >" (get (cheshire/decode (client/<!! c6) true) :name) "<" )
    )


  (def task-map
    {:name "Kevin"
     :desc "Description"})

  (println "task-map: " task-map)
  (def task-json (cheshire/encode task-map))
  (def task-map2 (cheshire/decode task-json true))
  (println task-map2)
  (println (get task-map2 :name) )
