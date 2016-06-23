(ns playground.core-spec
  (:require [playground.core :refer :all]
            [speclj.core :refer :all]
            )
  (:use clojure.java.io)
  )

(describe "playground tests"
  (it "adds 1 to input"
      (let [result (adds-one 2)]
        (should= 3 result)))

  (it "tests multiple-let"
      (let [result (multiple-let 2)]
        (should= 12 result)))

  (it "test a response from the app"
      (let [request (hash-map :request-method :get
                              :uri "/")
            response (player request)]
        ;(println response)))
        (should= "777" ((:headers response) "Age") )))

  (it "test a response from just the handler"
      (let [request (hash-map :request-method :get )
            response (handler request)]
        (should= 200 (:status response) )))

  (it "test a response field from just a POST middleware"
      (let [handler (wrap-post identity)]
        (let [request  {:request-method :post}
              response (handler request)]
          (should= "POST snatcher!"  (:body response)))))

  (it "test an entire response from just a middleware"
      (let [handler (wrap-body-replace identity)]
        (let [request {}
              response (handler request)]
          (should= {:body "Body snatcher is scary!"}  (identity response)))))

  (it "test wrap-post method specific modification POST"
      (let [handler (wrap-post identity)]
        (let [request  {:request-method :post :headers {"content-type" "application/xml"} }
              response (handler request)]
          (should= "POST snatcher!"  (:body response)))))

  (it "test GET default"
    (let [request  {:request-method "GET" :headers {"content-type" "application/xml"} }
          response (handler request)]
      (should= "Kevin, you freak!"  (:body response))))

  (it "gets file resource response from specific uri"
    (let [request (hash-map :uri "/kevin.html"
                    :request-method :get )
          response (player request)]
      (with-open [rdr (reader (:body response))]
        (doseq [line (line-seq rdr)]
          ;(println line)))))
          (should= "<p>Kevin was here.</p>" line )))))
)
