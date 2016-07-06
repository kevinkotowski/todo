(ns api.core-spec
  (:require [api.core :as api]
            [speclj.core :refer :all]
            [clojure.string :as string])
  )

(describe "api module"
  (def db "TEST")
  (def entity "tasks")

  (it "proves that api.core_spec works"
    (should= true true)
    )

  ;(it "has an HTML safe pair to hash converter"
  ;    ;(let [])
  ;  (def nilVal "done=")
  ;  (def zeroVal "done=0")
  ;  (def textVal "desc=I+am+cool")
  ;  (should= {:done nil} (api/pairToHash nilVal))
  ;  (should= {:done "0"} (api/pairToHash zeroVal))
  ;  (should= {:desc "I am cool"} (api/pairToHash textVal))
  ;  )
  ;
  (it "has a val to URL safe converter"
    (def nilVal nil)
    (def zeroVal "0")
    (def textVal "I am cool")
    (should= "" (api/toSafeVal nilVal) )
    (should= "0" (api/toSafeVal zeroVal) )
    (should= "I+am+cool" (api/toSafeVal textVal) )
    )

  (it "creates a new record from safe string no done"
      ;(println "-->no done: starting")
      ;    (let [ id (api/create db entity "desc=New+task")
        (let [ id (api/create db entity {"desc" "New task"} )
               ;dummy (println "-->no done id: " id)
               task (api/read db entity id) ]
               (should (string/starts-with? task "done=0&desc=New+task") )
               (should (string/starts-with? id (str db ":" entity) ) )
        ) )

  (it "creates a new record from safe string nil done"
    ;(def newHtml "done=&desc=New+task")
    (def id (api/create db entity {"done" nil "desc" "New task"}) )
    (def task (api/read db entity id) )
    (should (string/starts-with? task "done=0&desc=New+task") )
    (should (string/starts-with? id (str db ":" entity) ) )
    )

  (it "gets all records"
    (def all (api/readAll db entity) )
    (should= (api/countAll db entity) (count all) )
    )

  (it "gets a single record"
    (def id (api/create db entity {"desc" "Single task"}) )
    (def task (api/read db entity id) )
    (should (string/starts-with? task "done=0&desc=Single+task") )
    )

  (it "updates a record"
    (let  [id (api/create db entity {"done" 0 "desc" "Original task"})
           task (api/read db entity id) ]
          (should (string/starts-with? task "done=0&desc=Original+task") )
          (api/update db entity id {"done" 1 "desc" "Updated description"} )
          (let [updatedTask (api/read db entity id)]
            (should (string/starts-with? updatedTask "done=1&desc=Updated+description") ) ) )
    )

  (it "deletes a record"
    (def id (api/create db entity {"desc" "Delete task"}) )
    (def task (api/read db entity id) )
    ; proves record was created successfully before we delete it
    (should (string/starts-with? task "done=0&desc=Delete+task") )

    (api/delete db entity id)
    (def deletedTask (api/read db entity id) )
    (should= nil deletedTask)
    )

  (after-all "deletes all test records"
    ;(let [ beforeCount (api/countAll db entity) ]
    ;  (should (> beforeCount 0) ) )
    (api/deleteAll db entity)
    (let [ afterCount (api/countAll db entity) ]
      (should= 0 afterCount) )
    )
)
