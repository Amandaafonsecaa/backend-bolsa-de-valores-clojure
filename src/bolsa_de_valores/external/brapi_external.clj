(ns bolsa-de-valores.external.brapi-external
  (:require [clj-http.client :as http] ;; usado pra fazer req. http
            [bolsa-de-valores.config :as config]))

;; consulta "atual" (sem considerar data)
(defn consulta [ticker] ;; ticker é código da ação
  (let [url (str config/brapi-api-url ticker)]
    (try 
      (let [response (http/get url {:as :json :throw-exceptions false})] ;; json -> map
        (if (= 200 (:status response))
          response
          (throw (ex-info "Erro na API Brapi" ;; ex-info cria exceções com dados estruturados
                          {:ticker ticker ;; chave e valor (hash-map)
                           :status (:status response)
                           :body (:body response)}))))
      (catch Exception e
        (throw (ex-info "Falha de conexão com API Brapi" 
                        {:ticker ticker
                         :erro (.getMessage e)}))))))

;; consulta histórica usando a data informada na transação
(defn consulta-historica
  "Consulta cotação histórica para um `ticker` em uma `data-str` (string).
   A função extrai apenas a parte da data (AAAA-MM-DD) caso venha com horário."
  [ticker data-str]
  (let [data (subs data-str 0 10)                                ;; pega só a parte da data
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
