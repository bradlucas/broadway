(defproject broadway "1.0.0"
  :description ""
  :url "http://example.com/FIXME"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.0"]
                 [hiccup "1.0.5"]

                 [org.clojure/tools.cli "0.3.5"]
                 [clj-http "3.1.0"]
                 [enlive "1.1.6"]

                 [ring/ring-jetty-adapter "1.5.0-RC1"]
                 [ring/ring-defaults "0.2.0"]
                 ]

  :plugins [[lein-ring "0.9.7"]]
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [ring/ring-mock "0.3.0"]]}
             :uberjar {:uberjar-name "broadway-standalone.jar"}}
  :ring {:handler broadway.handler/app}
  :main broadway.core)
