(ns broadway.data
  (:require [clojure.edn :as edn]
            [net.cgrand.enlive-html :as html]
            [clj-http.client :as client]))


;; Url to lottery status page
(def ^:dynamic *url* "https://lottery.broadwaydirect.com/check-status")

(defn- get-latest-dlslot-nonce 
  "Call the page with the form and extract the current hidden code needed for a valid post

  <input type='hidden' id='dlslot_nonce' name='dlslot_nonce' value='178c165e7e'>
  "
  []
  (let [value (:value (:attrs (first (html/select (html/html-resource (java.io.StringReader. (:body (client/get *url*)))) [:input#dlslot_nonce]))))]
    {:dlslot_nonce value}))

(defn- build-form-data
  [last-name email mth day year]
  {:dlslot_name_last last-name
   :dlslot_email email
   :dlslot_dob_month mth
   :dlslot_dob_day day
   :dlslot_dob_year year})

(defn do-post
  "Post user information to the Lottery check status page and return the results in a Dom (hash map)"
  ([last-name email mth day year]
   (let [result (client/post *url* {:form-params (merge (build-form-data last-name email mth day year) (get-latest-dlslot-nonce))})
         body (:body result)
         dom (html/html-resource (java.io.StringReader. body))]
     dom))
  ([user-info]
   (let [{:keys [last-name email mth day year]} user-info]
     (do-post last-name email mth day year))))

(defn- trim-content
  [r]
  (if (= 3 (count r))
    (clojure.string/trim (first (:content (second r))))
    (clojure.string/trim (first r))))

(defn extract-results
  "Extract the results for a given a Dom returned from do-post

Results are returned as a sequence of pairs.

((\"WICKED - 06/04/16, 2:00 pm\"
  \"Lottery Still Open / Winners To Be Selected Today at 10:30 am\")
 (\"WICKED - 06/04/16, 8:00 pm\"
  \"Lottery Still Open / Winners To Be Selected Today at 2:00 pm\")
 (\"HAMILTON - 06/04/16, 2:00 pm\"
  \"Lottery Still Open / Winners To Be Selected Today at 11:00 am\"))
"
  [dom]
  (vec (partition 2                                                                             ;; turn into pairs
                  (map #(trim-content %)                                                        ;; trim the values                   
                       (map #(:content %)                                                       ;; pull out the :content values
                            (html/select (html/select dom [:div#lottery-results])[:table :td])  ;; extract the sections of the dom with resuls
                            )))))

(defn get-results
  [user-info]
  (let [dom (do-post user-info)
        results (extract-results dom)]
    ;;results
    (assoc user-info :dom dom :results results)))

(defn get-family-results
  ([] (get-family-results "resources/config.edn"))
  ([file] (let [r (edn/read-string (slurp file))
                keys (sort (keys r))]
            (map #(get-results (% r)) keys))))


;; Command line report version
(defn print-family-report
  []
  (doseq [results (get-family-results)]
    (println (format "%s\n%s\n" (:name results) 
                     (clojure.string/join " " (:results results))))))


