(ns present.tasks-spec
  (:require [todo.core :refer :all]
            [clostache.parser :as parser]
            [present.tasks :as present]
            [speclj.core :refer :all]
            ) )

(defn mock-handler [request]
  {:status 200
   :body (str "mock body")})

(def mock-api
  (-> mock-handler
      (wrap-api-router) ) )

(describe "presenter"
    (it "isolates translation of clostache tags"
        (let [response (str "Iso {{id}}, you are {{done}} when {{desc}}")
              data {:id "XX:YYY:123" :done "0" :desc "Test desc"}
              response (parser/render response data)]
          (should= "Iso XX:YYY:123, you are 0 when Test desc" response) ) )

    (it "translate clostache tags in real single task"
        (let [request (hash-map :uri "/api/tasks/create"
                                :params {"done" "0" "desc" "Real task"}
                                :request-method :post)
              taskResponse (mock-api request)
              id (:body taskResponse)
              testResponse {:body "Real {{id}}, you are {{done}} when {{desc}}"}
              transformed (present/transform testResponse id)
              ]
          ;(println "...present.task_spec id: " id)
          ;(println "...present.task_spec single transformed: " (:body transformed) )
          (should (.contains (:body transformed) "Real TODO:tasks:" ) )
          (should (.contains (:body transformed) "you are 0 when Real task") )
          ) )

    (it "translate clostache tags in real multiple tasks"
        (let [firstRequest (hash-map :uri "/api/tasks/create"
                                :params {"done" "0" "desc" "First task"}
                                :request-method :post)
              firstId (:body (mock-api firstRequest))
              secondRequest (hash-map :uri "/api/tasks/create"
                                :params {"done" "0" "desc" "Second task"}
                                :request-method :post)
              secondId (:body (mock-api secondRequest))
              testResponse {:body "{{#tasks}}Multiple {{id}}, task {{desc}} is {{done}}.\n{{/tasks}}"}
              transformed (present/transform testResponse nil)
              ]
          ;(println "...present.task_spec firstId: " firstId)
          ;(println "...present.task_spec secondId: " secondId)
          ;(println "...present.task_spec multiple transformed: " transformed)
          (should (.contains (:body transformed) "Multiple TODO:tasks:") )
          (should (.contains (:body transformed) firstId) )
          (should (.contains (:body transformed) secondId) )
          ) )
)
