(defproject todo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring "1.5.0"]
                 [ring/ring-mock "0.3.0"]
                 [de.ubercode.clostache/clostache "1.4.0"]
                 [speclj "3.3.2"]]
  :dev-dependencies [[ring/ring-devel "1.5.0"]
                     ]
  :plugins [[speclj "3.3.2"]]
  :test-paths ["spec"]
  :main todo.core)
