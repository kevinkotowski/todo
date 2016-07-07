(ns todo.core-spec
  (:require [todo.core :refer :all]
            [speclj.core :refer :all]))

(defn mock-handler [request]
  {:status 200
   :body (str "mock body")})

(def mock-api
  (-> mock-handler
      (wrap-api-router)))

(def mock-template
  (-> mock-handler
      (wrap-template-router)))

(describe "todo app"
  (it "redirect home to /task/list template"
    (let [request (hash-map :uri "/"
                            :request-method :get)
          response (app request)]
      (should-be true? (.contains (:body response) "<title>Tasks</title>"))))

  (it "isolated wrap-api-router middleware testing"
    (let [request (hash-map :uri "/api/tasks/create"
                            :params {"done" "0" "desc" "Kevin"}
                            :request-method :post)
          response (mock-api request)
          id (:body response)]
      (should (.contains (:body response) ":tasks:"))
      (let [request (hash-map :uri (str "/api/tasks/update/" id)
                              :params {"done" 1 "desc" "Kevin updated"}
                              :request-method :post)
            response (mock-api request)]
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
          id (:body response)]
      (let [request (hash-map :uri (str "/tasks/edit/" id)
                              :request-method :get)
            response (mock-template request)]
        ;(println "...task_spec template: " response)
        (should-be true? (.contains (:body response)
                                    "<title>Edit Task</title>")))
      (let [request (hash-map :uri (str "/api/task/delete/" id)
                              :request-method :post
                              )
            response (mock-api request)]
        (should= 302 (:status response)))))
)
