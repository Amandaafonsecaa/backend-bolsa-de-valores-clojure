(ns bolsa-de-valores.external.brapi-external
  (:require [clj-http.client :as http]
            [cheshire.core :as json]))

(defn consulta [ticker]
  (let [url (str "https://brapi.dev/api/quote/" ticker)]
    (http/get url {:as :json})))
