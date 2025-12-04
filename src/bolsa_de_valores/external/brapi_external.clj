(ns bolsa-de-valores.external.brapi-external
  (:require [clj-http.client :as http]
            [bolsa-de-valores.config :as config]))

(defn consulta [ticker]
  (let [url (str config/brapi-api-url ticker)]
    (try 
      (let [response (http/get url {:as :json :throw-exceptions false})]
        (if (= 200 (:status response))
          response
          (throw (ex-info "Erro na API Brapi"
                          {:ticker ticker
                           :status (:status response)
                           :body (:body response)}))))
      (catch Exception e
        (throw (ex-info "Falha de conexão com API Brapi" 
                        {:ticker ticker
                         :erro (.getMessage e)}))))))

(defn consulta-historica
  [ticker data-str]
  (let [data (subs data-str 0 10)
        url  (str config/brapi-api-url
                  ticker
                  "?range=1d&interval=1d&from=" data "&to=" data "&historical=true")]
    (try
      (let [response (http/get url {:as :json :throw-exceptions false})]
        (if (= 200 (:status response))
          response
          (throw (ex-info "Erro na API Brapi (histórico)"
                          {:ticker ticker
                           :data   data
                           :status (:status response)
                           :body   (:body response)}))))
      (catch Exception e
        (throw (ex-info "Falha de conexão com API Brapi (histórico)"
                        {:ticker ticker
                         :data   data
                         :erro   (.getMessage e)}))))))
