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

  (it "has an HTML safe pair to hash converter"
      (let [])
    (def nilVal "done=")
    (def zeroVal "done=0")
    (def textVal "desc=I+am+cool")
    (should= {:done nil} (api/pairToHash nilVal))
    (should= {:done "0"} (api/pairToHash zeroVal))
    (should= {:desc "I am cool"} (api/pairToHash textVal))
    )

  (it "has a val to URL safe converter"
    (def nilVal nil)
    (def zeroVal "0")
    (def textVal "I am cool")
    (should= "" (api/toSafeVal nilVal) )
    (should= "0" (api/toSafeVal zeroVal) )
    (should= "I+am+cool" (api/toSafeVal textVal) )
    )

  (it "creates a new record from HTML"
    (def newHtml "done=&desc=New+task")
    (def id (api/create db entity newHtml) )
    (def task (api/read db entity id) )
    (should= "done=0&desc=New+task" task )
    (should (string/starts-with? id (str db ":" entity) ) )
    )

  (it "gets all records"
    (def all (api/readAll db entity) )
    (should= (api/countAll db entity) (count all) )
    )

  (it "gets a single record"
    (def desc (str "desc=Single+task") )
    (def id (api/create db entity desc) )
    (def task (api/read db entity id) )
    (should= (str "done=0&" desc)  task )
    )

  (it "updates a record"
    (let  [desc (str "desc=Update+task")
           newDesc (str "desc=Updated+description")
           id (api/create db entity desc)
           task (api/read db entity id) ]
          (should= (str "done=0&" desc) task )
          (api/update db entity id (str "done=1&" newDesc) )
          (let [updatedTask (api/read db entity id)]
            (should= (str "done=1&" newDesc) updatedTask) ) )
    )

  (it "deletes a record"
    (def desc (str "desc=Delete+task") )
    (def id (api/create db entity desc) )
    (def task (api/read db entity id) )
    ; proves record was created successfully before we delete it
    (should= (str "done=0&" desc) task )

    (api/delete db entity id)
    (def deletedTask (api/read db entity id) )
    (should= nil deletedTask)
    )

  (after-all "deletes all test records"
    (def beforeCount (api/countAll db entity) )
    (should (> beforeCount 0) )
    (api/deleteAll db entity)
    (def afterCount (api/countAll db entity) )
    (should= 0 afterCount)
    )
)
