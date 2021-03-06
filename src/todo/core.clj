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
  (:require [present.tasks :as present])
 )

(def db "TODO")

(defn wrap-api-router [handler]
  (fn [request]
    ;(println "ACCESS:" (:request-method request) (:uri request) )
    (if (= (:request-method request) :post)
      (let [[root, api, entity, operation, id]
              (split (:uri request) #"\/")
            params (:params request)]
        (case operation
          "create" (def newId (api/create db entity params) )
          "update" (api/update db entity id params)
          "delete" (api/delete db entity id) )
        {:status 302
         :body (if (empty? id) newId id)
         :headers {"Content-Type" "text/plain" "Location" "/"} } )
      (let [response (handler request)] response) ) ) )

(defn wrap-template-router [handler]
  (fn [request]
    ;(println "ACCESS:" (:request-method request) (:uri request) )
    (if (= (:request-method request) :get)
      (let [[root, entity, operation, id]
            (split (:uri request) #"\/")
            template (str entity "/" operation)
            response (handler request)

            path (str "templates/" template ".mustache")
            file (clojure.java.io/resource path)
            body (slurp file)

            response (assoc-in response [:headers "Content-Type"] "text/html")
            response (assoc response :body body)
            response (present/transform response id)
            ]
        (identity response)
        )
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

(def app
  (-> handler
       (wrap-content-type)
       (wrap-api-router)
       (wrap-template-router)
       (wrap-home-redirect)
       (wrap-params)
       (wrap-not-modified)
       (wrap-reload) ) )

(defn boot []
  (run-jetty #'app {:port 50000 :join? false}) )

(use '[clojure.tools.namespace.repl :only (refresh)] )