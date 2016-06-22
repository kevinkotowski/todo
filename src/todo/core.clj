(ns todo.core
  (:use ring.middleware.reload)
  (:use ring.adapter.jetty)
  (:use ring.middleware.resource)
  (:use ring.middleware.content-type)
  (:use ring.middleware.not-modified))


(defn adds-one [x]
  (+ x 1))

(defn adds-two [x]
  (+ x 2))

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
    (println request)
    (let [response (handler request)]
      (assoc-in response [:headers  "Age"] "777")
      )))

(defn wrap-body-replace [handler]
  (fn [request]
    (println request)
    (let [response (handler request)]
      (assoc response :body "Body snatcher!")
      )))

(defn handler [req]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (str "Kevin, you freak!") })

(def app
   (-> handler
       (wrap-body-replace)
       (wrap-age)
       (wrap-auth)
       (wrap-resource "public")
       (wrap-content-type)
       (wrap-not-modified)
       (wrap-reload) ))

(defn boot []
  (run-jetty #'app {:port 54321 :join? false}))

(use '[clojure.tools.namespace.repl :only (refresh)])