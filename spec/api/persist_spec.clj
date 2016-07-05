(ns api.persist-spec
  (:require [api.persist :as persist]
            [speclj.core :refer :all]
            [clojure.string :as string])
)

(describe "persist module"
  (def db "TEST")
  (def entity "tasks")

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
    (def countBefore (persist/countAll db entity) )
    (def newHash {:done 0 :desc "New persist record"} )
    (def id (persist/createOne db entity newHash) )
    (def task (persist/getOne db entity id) )
    (should= "0" (task :done) )
    (should= (newHash :desc) (task :desc) )
    (should (string/starts-with? id (str db ":tasks:") ) )
    (should (> (persist/countAll db entity) countBefore) )

    )

  (it "gets all records"
    (def all (persist/getAll db entity) )
    (should= (persist/countAll db entity) (count all) )
    ;(println (nth all 4) )
    ;(println (count all) )
    )

  (it "gets a single record"
    (def getHash {:desc "Single persist record"} )
    (def id (persist/createOne db entity getHash) )
    (def dbHash (persist/getOne db entity id) )
    (should= "0" (dbHash :done) )
    (should= (getHash :desc)  (dbHash :desc) )
    )

  (it "updates a record"
    (def origHash {:desc "Update persist record"} )
    (def newHash {:done 1 :desc "Updated description"} )
    (def id (persist/createOne db entity origHash) )
    (def dbHash (persist/getOne db entity id) )
    ; just verifying the original task before update
    (should= "0" (dbHash :done) )
    (should= (:desc origHash) (dbHash :desc) )

    (persist/updateOne db entity id newHash )
    (def updatedHash (persist/getOne db entity id) )
    (should= "1" (updatedHash :done) )
    (should= (newHash :desc) (updatedHash :desc) )
    )

  (it "deletes a record"
    (def delHash {:desc "Delete persist record"} )
    (def id (persist/createOne db entity delHash) )
    (def dbHash (persist/getOne db entity id) )
    ; proves record was created successfully before we delete it
    (should= "0" (dbHash :done) )
    (should= (delHash :desc) (dbHash :desc) )

    (persist/deleteOne db entity id)
    (def deleteHash (persist/getOne db entity id) )
    (should= nil deleteHash )
    )

  (after-all "deletes all test records"
    (def beforeCount (persist/countAll db entity) )
    (should (> beforeCount 0) )
    (persist/deleteAll db entity)
    (def afterCount (persist/countAll db entity) )
    (should= 0 afterCount)
    )

)