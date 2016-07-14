(ns api.persist-spec
  (:require [api.persist :as persist]
            [speclj.core :refer :all]
            [clojure.string :as string])
)

(describe "persist module"
  (def db "TEST")
  (def entity "tasks")

  (it "gets new ID"
    (let [ oldID (persist/getId)
           newID (persist/getId)
           newerID (persist/getId) ]
      (should= newID (+ oldID 1) )
      (should= newerID (+ newID 1) ) ) )


  (it "creates a new record"
    (let [
          countBefore (persist/countAll db entity)
          newHash {:done 0 :desc "New persist record"}
          id (persist/createOne db entity newHash)
          task (persist/getOne db entity id)
          ]
      (should= "0" (task :done) )
      (should= (newHash :desc) (task :desc) )
      (should (string/starts-with? id (str db ":tasks:") ) )
      (should (> (persist/countAll db entity) countBefore) ) ) )

  (it "gets all records"
    (let [ all (persist/getAllIds db entity) ]
      (should= (persist/countAll db entity) (count all) )
      )
    )

  (it "gets a single record"
    (let [ getHash {:desc "Single persist record"}
           id (persist/createOne db entity getHash)
           dbHash (persist/getOne db entity id) ]
      (should= "0" (dbHash :done) )
      (should= (getHash :desc)  (dbHash :desc) ) ) )

  (it "updates a record"
    (let [
          origHash {:desc "Update persist record"}
          newHash {:done 1 :desc "Updated description"}
          id (persist/createOne db entity origHash)
          dbHash (persist/getOne db entity id)
          ]
      (should= "0" (dbHash :done) )
      (should= (:desc origHash) (dbHash :desc) )

      (persist/updateOne db entity id newHash )
      (let [updatedHash (persist/getOne db entity id) ]
        (should= "1" (updatedHash :done) )
        (should= (newHash :desc) (updatedHash :desc) ) ) ) )

  (it "deletes a record"
    (let [ delHash {:desc "Delete persist record"}
           id (persist/createOne db entity delHash)
           dbHash (persist/getOne db entity id) ]
      (should= "0" (dbHash :done) )
      (should= (delHash :desc) (dbHash :desc) )
      (persist/deleteOne db entity id)

      (let [
            deleteHash (persist/getOne db entity id)
            ]
        (should= nil deleteHash ) ) ) )

  (after-all "deletes all test records"
    (let [ beforeCount (persist/countAll db entity) ]
      (should (> beforeCount 0) )
      (persist/deleteAll db entity)
      (let [ afterCount (persist/countAll db entity) ]
        (should= 0 afterCount) ) ) )
)