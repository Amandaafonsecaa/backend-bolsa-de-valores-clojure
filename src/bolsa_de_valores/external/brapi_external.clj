(ns bolsa-de-valores.clients.brapi-client
  (:require [clj-http.client :as http]
            [cheshire.core :as json]))

(defn consulta [ticker]
  (let [url (str "https://brapi.dev/api/quote/" ticker)
        resp (http/get url {:as :json})]
    (get-in resp [:body :results 0 :regularMarketPrice])))
