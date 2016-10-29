(ns broadway.views
  (:require [clojure.string :refer [capitalize]]
            [hiccup.core :refer [html]]
            [hiccup.element :refer [link-to]]
            [hiccup.page :refer [html5 include-css include-js]]
            [broadway.data :as data]))


(defn common [& body] (html5
                       [:head
                        [:title "Broadway Lottery"] 
                        (include-js "//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js")
                        (include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css")
                        (include-css "/css/sticky.css")
                        (include-css "/css/screen.css")
                        ]
                       [:body
                        [:div {:id "wrap"}
                         [:div {:class "navbar navbar-inverse navbar-fixed-top"}
                          [:div {:class "container"}
                           [:div {:class "nav-header"}
                            [:a {:class "brand" :href "/"} "Broadway"]]]]
                         [:div {:class "container"}
                          [:div {:class "page-header"}
                           [:h2 "Broadway"]]
                          "%BODY%"
                          ]
                         [:div {:class "container"}
                          (link-to {:class "btn btn-primary" :target "blank"}  "https://lottery.broadwaydirect.com" "Broadway Direct") 
                          (link-to {:class "btn btn-primary" :target "blank"}  "https://lottery.broadwaydirect.com/show/hamilton" "Hamilton Page") 
                          ;; todo Get the "Enter Now" button url from https://lottery.broadwaydirect.com/show/hamilton/ dynamically
                          (link-to {:class "btn btn-primary" :target "blank"} "https://lottery.broadwaydirect.com/enter-lottery/?lottery=165618" "Entry Form")]
                        ]
                        [:div {:id "footer"}
                         [:div {:class "container"}
                          [:p "Copyright 2016 Brad Lucas" ]]]
                        ]))

(defn name-div [name email]
  [:header {:class "panel-heading"}
   [:h4 {:class "panel-title"} (str name " - " email)]])

(defn result-div [result]
  [:div {:class "panel-body"}
   [:p (clojure.string/join " " result)]])

(defn results-div [results]
  (map result-div results))

(defn family-results
  []
  (letfn [(result-div [results]
            [:section {:class "panel panel-default"}
             (name-div (:name results) (:email results))
             (results-div (:results results))
             ]
            )
          (result-blocks [results] (mapv result-div results))]
  (apply str (map #(html %) (result-blocks (data/get-family-results))))))


(defn build-page
  [common body]
  (clojure.string/replace common #"%BODY%" body))

(defn home 
  [] 
  (build-page (common) (family-results)))

