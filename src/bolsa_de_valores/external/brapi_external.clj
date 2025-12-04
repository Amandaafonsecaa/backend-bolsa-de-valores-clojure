(ns bolsa-de-valores.external.brapi-external
  (:require [clj-http.client :as http] ;; usado pra fazer req. http
            [bolsa-de-valores.config :as config]))

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
