(ns todo.persist-spec
  (:require [todo.persist :as persist]
            [speclj.core :refer :all]
   )
)

(describe "persist module"
  (def todoset "TEST")
  (it "proves that persist.core_spec works"
    (should= true true)
      )

  (it "gets new ID"
    (def oldID (persist/getId) )
    (def newID (persist/getId) )
    (def newerID (persist/getId) )
    (should= newID (+ oldID 1))
    (should= newerID (+ newID 1))
      )

  (it "creates a new record"
    (def countBefore (persist/countAll todoset) )
    (should (persist/create todoset "New test task"))
    (should (> (persist/countAll todoset) countBefore ))
      )

  (it "gets all records"
    (def all (persist/getAll todoset))
    (should= (persist/countAll todoset) (count all) )
      ;(print (first all))
      (doseq [x all] (println x))
      ;(doseq [x all] (println (get x "id")))
      )

  ;(it "gets a single record"
  ;  ;(should= true true)
  ;    )
  ;
  ;(it "updates a record"
  ;  ;(should= true true)
  ;    )
  ;
  ;(it "deletes a record"
  ;  ;(should= true true)
  ;    )

  ;(it "deletes all records"
  ;  ;(should= true true)
  ;    )
  ;
)