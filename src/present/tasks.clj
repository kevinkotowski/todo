(ns present.tasks
  (:require [clostache.parser :as parser] )
  (:require [api.persist :as persist])
)

(defn transform [response id]
  (let [response (assoc-in response [:headers "Content-Type"] "text/html")
        data (if (= nil id) {} (persist/getOne "TODO" "tasks" id))
        data (assoc data :tasks (persist/getAll "TODO" "tasks"))
        ]
    ;(println "...present.tasks.transform data: " data)
    ;(println "...present.tasks.transform after: " response)
    ;    (parser/render response data)
        (assoc response :body (parser/render (:body response) data))
    )
  )

