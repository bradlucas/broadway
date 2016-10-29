(ns broadway.core
  (:gen-class)
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [broadway.handler :as handler]))


(defn -main [& args]
  (let [ip (get (System/getenv) "BROADWAY_WEB_IP" "0.0.0.0")
        port (Integer/parseInt (get (System/getenv) "BROADWAY_WEB_PORT" "5000"))]
    (run-jetty handler/app {:host ip :port port})))

