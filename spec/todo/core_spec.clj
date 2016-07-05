(ns todo.core-spec
  (:require [todo.core :refer :all]
            [ring.mock.request :as mock]
            ;[clojure.java.io :as io]
            [speclj.core :refer :all]
            )
  (:use clojure.java.io)
  (:import (java.io BufferedInputStream)))

(defn mock-handler [request]
  {:status 200
   :body (str "mock body")})

(def mock-api
  (-> mock-handler
      (wrap-api-router) ) )

(defn get-bytes [inString]
  (BufferedInputStream. (make-input-stream inString (:encoding "UTF-8")))
  )

(describe "todo app"
  (it "redirect home to /task/list template"
    (let [request (hash-map :uri "/"
                            :request-method :get)
          response (app request)]
      (should-be true? (.contains (:body response) "<title>Tasks</title>"))))

  (it "isolated wrap-api middleware testing"
    (let [request (hash-map :uri "/api/task/create"
                            :form-params (get-bytes "ascii=kev")
                            :request-method :post)
          response (mock-api request)]
      (should= "POST::update snatcher!"  (:body response)))
    ;(let [request (hash-map :uri "/api/task/update/11111"
    ;                        :request-method :post)
    ;      response (mock-api request)]
    ;  (should= "POST::update snatcher!"  (:body response)))
      )

  ;(it "isolated wrap-api middleware DELETE testing"
  ;  (let [request (hash-map :uri "/api/task/delete/378901"
  ;                          :request-method :post
  ;                          :form-params (get-bytes "delete=snatcher")
  ;                          ;:form-params (bytes (byte-array (map (comp byte int) "ascii=kev")))
  ;                          )
  ;        response (mock-api request)]
  ;    (should= "POST::delete snatcher!"  (:body response))))

  (it "template middleware routes no ID to templates directory"
    (let [request (hash-map :uri "/tasks/new"
                            :request-method :get)
          response (app request)]
      ;(println (:status response))))
      (should-be true? (.contains (:body response) "<title>New Task</title>"))))

  (it "template middleware routes with ID to templates directory"
    (let [request (hash-map :uri "/tasks/edit/328947"
                            :request-method :get)
          response (app request)]
      ;(println (:status response))))
      (should-be true? (.contains (:body response) "<title>Edit Task</title>") )))
)
