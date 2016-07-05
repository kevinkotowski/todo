(ns todo.core
  (:use ring.middleware.reload)
  (:use ring.adapter.jetty)
  (:use ring.middleware.resource)
  (:use ring.middleware.content-type)
  (:use ring.middleware.not-modified)
  (:use ring.middleware.params)
  (:use clostache.parser)
  (:use [clojure.string :only (join split)])
  (:require [api.core :as api])
 )

(def db "TODO")

(defn wrap-api-router [handler]
  (fn [request]
    (if (= (:request-method request) :post)
      (let [[root, api, entity, operation, id]
              (split (:uri request) #"\/")
            body (:form-params request)]
        (println "...todo.core.wrap-api-router body: " body)
        (println "...todo.core.wrap-api-router params: " (:params request))
        (case operation
            "create" (api/create db entity body)
            "update" (api/update db entity id body)
            "delete" (api/delete db entity id)
            (def body (str "Bad operation: " operation) ) )
        {:status 200
         :body body
         :headers {"Content-Type" "text/plain"} }
        )
      (let [response (handler request)] response) ) ) )

(defn wrap-router [handler]
  (fn [request]
    ;(println "ACCESS:" (:request-method request) (:uri request) )
    (if (= (:request-method request) :get)
      (let [template (let [[root, entity, operation, id]
                           (split (:uri request) #"\/")]
                       (str entity "/" operation) )
                       ;(str "tasks/edit") )
            path (str "templates/" template ".mustache")
            response (handler request)
            response (assoc-in response [:headers "Content-Type"] "text/html")
            response (assoc response :body (render-resource path {:name "Kevin"}))
            ]
        response )
      (let [response (handler request)] response) ) ) )

(defn wrap-home-redirect [handler]
  (fn [request]
    (if (and
          (= (:uri request) "/")
          (= (:request-method request) :get ) )
      (let [request (assoc request :uri "/tasks/list")
            response (handler request)] response)
      (let [response (handler request)] response)
    ) ) )

(defn handler [req]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (str "Kevin, you freak!") } )
   ;})

(def app
  (-> handler
       (wrap-resource "public")
       (wrap-content-type)
       (wrap-api-router)
       (wrap-router)
       (wrap-home-redirect)
       (wrap-params)
       (wrap-not-modified)
       (wrap-reload) ) )

(defn boot []
  (run-jetty #'app {:port 50000 :join? false}) )

(use '[clojure.tools.namespace.repl :only (refresh)] )