(ns todo.core
  (:use ring.middleware.reload)
  (:use ring.adapter.jetty)
  (:use ring.middleware.resource)
  (:use ring.middleware.content-type)
  (:use ring.middleware.not-modified)
  (:use clostache.parser)
  (:require [taoensso.carmine :as car :refer (wcar)])
  )

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

(def app
  (-> handler
       (wrap-post)
       (wrap-template)
       (wrap-resource "public")
       (wrap-content-type)
       (wrap-not-modified)
       (wrap-reload) ))

(defn boot []
  (run-jetty #'app {:port 50000 :join? false}))

(use '[clojure.tools.namespace.repl :only (refresh)])