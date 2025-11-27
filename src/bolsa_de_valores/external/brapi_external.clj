(ns bolsa-de-valores.external.brapi-external
  (:require [clj-http.client :as http]))

(defn consulta [ticker]
  (let [url (str "https://brapi.dev/api/quote/" ticker)]
    (http/get url {:as :json})))
