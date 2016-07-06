(ns todo.core-spec
  (:require [todo.core :refer :all]
            ;[ring.mock.request :as mock]
            ;[clojure.java.io :as io]
            [speclj.core :refer :all]
            )
  ;(:use clojure.java.io)
  (:import (java.io BufferedInputStream ByteArrayInputStream)))

(defn mock-handler [request]
  {:status 200
   :body (str "mock body")})

(def mock-api
  (-> mock-handler
      (wrap-api-router) ) )

(describe "todo app"
  (it "redirect home to /task/list template"
    (let [request (hash-map :uri "/"
                            :request-method :get)
          response (app request)]

      (println "...todo.core_spec redirect body: " (:body response) )
      (should-be true? (.contains (:body response) "<title>Tasks</title>"))))

  (it "isolated wrap-api-router middleware testing"
    (let [request (hash-map :uri "/api/tasks/create"
                            :params {"done" "0" "desc" "Kevin"}
                            :request-method :post)
          response (mock-api request)
          id (:body response)]
      (println "...task_spec_api id: " id)
      (should (.contains (:body response) ":tasks:"))
      (let [request (hash-map :uri (str "/api/tasks/update/" id)
                              :params {"done" 1 "desc" "Kevin updated"}
                              :request-method :post)
            response (mock-api request)]
        ;(println "...task_spec_api response: " response)
        (should= id (:body response))
        (let [request (hash-map :uri (str "/api/task/delete/" id)
                                :request-method :post
                                )
              response (mock-api request)]
          (should= 302 (:status response))
        ))))

  (it "template middleware routes to templates directory"
    (let [request (hash-map :uri "/api/tasks/create"
                            :params {"done" 0 "desc" "Template test"}
                            :request-method :post)
          response (mock-api request)
          id (:body response)
          ]
      (println "...task_spec templates id: " id)
      (let [request (hash-map :uri (str "/api/tasks/list")
                              :request-method :get)
            response (mock-api request)]
        ;(println "...task_spec template: " response)
        ;(should-be true? (.contains (:body response) "<title>Edit Task</title>"))
         )
      )
    ;(let [request (hash-map :uri "/tasks/new"
    ;                        :request-method :get)
    ;      response (app request)]
    ;  ;(println (:status response))))
    ;  (should-be true? (.contains (:body response) "<title>New Task</title>"))))

  ;(it "template middleware routes with ID to templates directory"
  ;  (let [request (hash-map :uri "/tasks/edit/328947"
  ;                          :request-method :get)
  ;        response (app request)]
  ;    ;(println (:status response))))
  ;    (should-be true? (.contains (:body response) "<title>Edit Task</title>") ))
   )
)
