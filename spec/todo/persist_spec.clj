(ns todo.persist-spec
  (:require [todo.core :refer :all]
            [todo.persist :refer :all]
            [speclj.core :refer :all]
            ;[monger.core :as mg]
            ;[monger.collection :as mc]
  ;(:import [org.bson.types ObjectId]
  ;         [com.mongodb DB WriteConcern])
   )
)

(describe "persist module"
  (it "proves that persist.core_spec works"
    (should= true true)
      )

  (it "creates a new record"
      ;(let [conn (mg/connect)
      ;      db   (mg/get-db conn "test")]
      ;  ;; with a generated document id, returns the complete
      ;  ;; inserted document
      ;  ;(mc/insert-and-return db "todo" {:name "John" :age 30})
      ;  (mc/insert db "tasks" { :_id (ObjectId.) :first_name "John" :last_name "Lennon" })
        )

  (it "gets all records"
    ;(should= true true)
      )

  (it "gets a single record"
    ;(should= true true)
      )

  (it "updates a record"
    ;(should= true true)
      )

  (it "deletes a record"
    ;(should= true true)
      )
)