(ns todo.core-spec
  (:require [todo.core :refer :all]
            [ring.mock.request :as mock]
            [speclj.core :refer :all]
            )
  (:use clojure.java.io)
  )

(describe "todo app"
  (it "gets custom body response field from a POST middleware"
    (let [handler (wrap-post identity)]
      (let [request  {:request-method :post}
            response (handler request)]
        (should= "POST snatcher!"  (:body response)))))

  (it "redirect home to /task/list template"
    (let [request (hash-map :uri "/"
                            :request-method :get )
          response (app request)]
      ;(println (:body response))
      (should-be true? (.contains (:body response) "<title>Tasks</title>") )))

  (it "template middleware routes no ID to templates directory"
    (let [request (hash-map :uri "/tasks/new"
                            :request-method :get )
          response (app request)]
      ;(println (:status response))))
      (should-be true? (.contains (:body response) "<title>New Task</title>") )))

  (it "template middleware routes with ID to templates directory"
    (let [request (hash-map :uri "/tasks/edit/328947"
                            :request-method :get )
          response (app request)]
      ;(println (:status response))))
      (should-be true? (.contains (:body response) "<title>Edit Task</title>") )))
)
