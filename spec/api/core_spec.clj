(ns api.core-spec
  (:require [api.core :as api]
            [speclj.core :refer :all]
            [clojure.string :as string])
  )

(describe "api module"
  (def db "TEST")
  (def entity "tasks")

  (it "has a val to URL safe converter"
    (let [ nilVal nil
           zeroVal "0"
           textVal "I am cool" ]
      (should= "" (api/toSafeVal nilVal) )
      (should= "0" (api/toSafeVal zeroVal) )
      (should= "I+am+cool" (api/toSafeVal textVal) ) ) )

  (it "creates a new record from safe string no done"
        (let [ id (api/create db entity {"desc" "New task"} )
               task (api/read db entity id) ]
           (should (string/starts-with? task "done=0&desc=New+task") )
           (should (string/starts-with? id (str db ":" entity) ) ) ) )

  (it "creates a new record from safe string nil done"
    (let [ id (api/create db entity {"done" nil "desc" "New task"} )
           task (api/read db entity id) ]
      (should (string/starts-with? task "done=0&desc=New+task") )
      (should (string/starts-with? id (str db ":" entity) ) ) ) )

  (it "gets all records"
    (let [ all (api/readAll db entity) ]
      (should= (api/countAll db entity) (count all) ) ) )

  (it "gets a single record"
    (let [ id (api/create db entity {"desc" "Single task"})
           task (api/read db entity id) ]
        (should (string/starts-with? task "done=0&desc=Single+task") ) ) )

  (it "updates a record"
    (let  [id (api/create db entity {"done" 0 "desc" "Original task"})
           task (api/read db entity id) ]
      (should (string/starts-with? task "done=0&desc=Original+task") )
      (api/update db entity id {"done" 1 "desc" "Updated description"} )
      (let [updatedTask (api/read db entity id)]
        (should (string/starts-with? updatedTask "done=1&desc=Updated+description") ) ) ) )

  (it "deletes a record"
    (let [ id (api/create db entity {"desc" "Delete task"} )
           task (api/read db entity id) ]
      (should (string/starts-with? task "done=0&desc=Delete+task") )

      (api/delete db entity id)
      (let [ deletedTask (api/read db entity id) ]
        (should= nil deletedTask) ) ) )

  (after-all "deletes all test records"
    (api/deleteAll db entity)
    (let [ afterCount (api/countAll db entity) ]
      (should= 0 afterCount) )
    )
)
