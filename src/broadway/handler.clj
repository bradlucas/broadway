(ns broadway.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [broadway.views :as views]))

(defroutes app-routes
  (GET "/" [] (views/home))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
