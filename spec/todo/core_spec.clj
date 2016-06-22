(ns todo.core-spec
  (:require [todo.core :refer :all]
            [speclj.core :refer :all]
            [ring.mock.request :as mock] )
  )

(describe "adds-one"
  (it "adds 1 to input"
    (let [result (adds-one 2)]
      (should= 3 result)))

;(describe "wrap-print-req prints the method"
;  (it "accepts a request"
;    (wrap-print-req) mock :put "/kevin" "is this content?"))

  (it "tests multiple-let"
      (let [result (multiple-let 2)]
        (should= 12 result)))

  (it "test a response from the app"
    (let [response (app mock/request)]
      (should= "777" ((:headers response) "Age") )))

  (it "test a response from just the handler"
    (let [response (handler mock/request)]
      (should= 200 (:status response) )))

  (it "test a response field from just a middleware"
    (let [handler (wrap-body-replace identity)]
       (let [request  {:headers {"content-type" "application/xml"} }
             response (handler request)]
         (should= "Body snatcher!"  (:body response)))))

  (it "test an entire response from just a middleware"
    (let [handler (wrap-body-replace identity)]
      (let [request {}
            response (handler request)]
        (should= {:body "Body snatcher!"}  (identity response)))))
)

