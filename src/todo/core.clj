(ns todo.core
  (:use ring.middleware.reload)
  (:use ring.adapter.jetty)
  (:use ring.middleware.resource)
  (:use ring.middleware.content-type)
  (:use ring.middleware.not-modified)
  (:use clostache.parser)
  (:use [clojure.string :only (join split)])
  ;(:require [taoensso.carmine :as car :refer (wcar)])
  (:use todo.persist)
 )


(defn wrap-post [handler]
  (fn [request]
    ;(println (str "...wrap-post found method: " (:request-method request)) )
    (if (true? (= (:request-method request) :post) )
      (let [response (handler request)]
        (assoc response :body "POST snatcher!") )
      (let [response (handler request)] response )
    )))

(defn wrap-router [handler]
  (fn [request]
    (if (= (:request-method request) :get)
        (let [
              template (let [
                             [root, entity, operation, id]
                             (split (:uri request) #"\/")]
                         (str entity "/" operation) )
                         ;(str "tasks/edit") )
              path (str "templates/" template ".mustache")
              response (handler request)
              response (assoc-in response [:headers "Content-Type"] "text/html")
              response (assoc response :body (render-resource path {:name "Kevin"}))
              ] response )
        (println ("request-method :get fails")))
    ))

(defn wrap-home-redirect [handler]
  (fn [request]
    (if (and
          (= (:uri request) "/")
          (= (:request-method request) :get ) )
      (let [request (assoc request :uri "/tasks/list")
            response (handler request)] response)
      (let [response (handler request)] response )
    )))

(defn handler [req]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (str "Kevin, you freak!") })
   ;})

(def app
  (-> handler
       (wrap-resource "public")
       (wrap-content-type)
       (wrap-post)
       (wrap-router)
       (wrap-home-redirect)
       (wrap-not-modified)
       (wrap-reload) ))

(defn boot []
  (run-jetty #'app {:port 50000 :join? false}))

(use '[clojure.tools.namespace.repl :only (refresh)])