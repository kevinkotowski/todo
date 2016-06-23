(ns todo.core-spec
  (:require [todo.core :refer :all]
            [speclj.core :refer :all]
            [ring.mock.request :as mock] )
  (:use clojure.java.io)
  )

(describe "todo app"
  (it "gets custom body response field from a POST middleware"
    (let [handler (wrap-post identity)]
      (let [request  {:request-method :post}
            response (handler request)]
        (should= "POST snatcher!"  (:body response)))))

  (it "gets file resource response from specific uri"
      (let [request
              (hash-map :uri "/kevin.html"
                        :request-method :get )
            response (app request)]
        (with-open [rdr (reader (:body response))]
          (doseq [line (line-seq rdr)]
            ;(println line)))
            (should= "<p>Kevin was here.</p>" line )))))
)
