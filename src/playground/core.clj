(ns playground.core
  (:use ring.middleware.reload)
  (:use ring.adapter.jetty)
  (:use ring.middleware.resource)
  (:use ring.middleware.content-type)
  (:use ring.middleware.not-modified)
  (:require [taoensso.carmine :as car :refer (wcar)])
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