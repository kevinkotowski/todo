(ns todo.persist-spec
  (:require [todo.persist :as persist]
            [speclj.core :refer :all]
            [clojure.string :as string])
)

(describe "persist module"
  (def todoenv "TEST")

  (it "proves that persist.core_spec works"
    (should= true true)
    )

  (it "gets new ID"
    (def oldID (persist/getId) )
    (def newID (persist/getId) )
    (def newerID (persist/getId) )
    (should= newID (+ oldID 1) )
    (should= newerID (+ newID 1) )
    )

  (it "creates a new record"
    (def countBefore (persist/countAll todoenv) )
    (should (string/starts-with?
              (persist/createOne todoenv "New test task")
              (str todoenv ":tasks:") ) )
    (should (> (persist/countAll todoenv) countBefore) )
    )

  (it "gets all records"
    (def all (persist/getAll todoenv) )
    (should= (persist/countAll todoenv) (count all) )
    ;(println (nth all 4) )
    ;(println (count all) )
    )

  (it "gets a single record"
    (def desc (str "Single task") )
    (def id (persist/createOne todoenv desc) )
    (def task (persist/getOne id) )
    (should= "0" (task :done) )
    (should= desc (task :desc) )
    )

  (it "updates a record"
    (def desc (str "Update task") )
    (def newDesc (str "Updated description") )
    (def id (persist/createOne todoenv desc) )
    (def task (persist/getOne id) )
    ; just verifying the original task before update
    (should= "0" (task :done) )
    (should= desc (task :desc) )

    (persist/updateOne id {:done 1 :desc newDesc} )
    (def updatedTask (persist/getOne id) )
    (should= "1" (updatedTask :done) )
    (should= newDesc (updatedTask :desc) )
    )

  (it "deletes a record"
    (def desc (str "Delete task") )
    (def id (persist/createOne todoenv desc) )
    (def task (persist/getOne id) )
    ; proves record was created successfully before we delete it
    (should= "0" (task :done) )
    (should= desc (task :desc) )

    (persist/deleteOne id)
    (def deletedTask (persist/getOne id) )
    (should= nil (deletedTask :done) )
    (should= nil (deletedTask :desc) )
    )

  (after-all "deletes all test records"
    (def beforeCount (persist/countAll todoenv) )
    (should (> beforeCount 0) )
    (persist/deleteAll todoenv)
    (def afterCount (persist/countAll todoenv) )
    (should= 0 afterCount)
    )
)