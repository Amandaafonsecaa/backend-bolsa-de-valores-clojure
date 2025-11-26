(ns bolsa-de-valores.services.cotacao_service
  (:require [bolsa-de-valores.external.brapi_external :as brapi]))

(defn consultar-preco [ticker]
  (let [resposta (brapi/consulta ticker)
        preco    (get-in resposta [:body :results 0 :regularMarketPrice])]
    preco))

(defn consultar-detalhes [ticker]
  (let [resposta (brapi/consulta ticker)]
    {:nome        (get-in resposta [:body :results 0 :longName])
     :nome-curto  (get-in resposta [:body :results 0 :shortName])
     :moeda       (get-in resposta [:body :results 0 :currency])
     :preco       (get-in resposta [:body :results 0 :regularMarketPrice])
     :alta-dia    (get-in resposta [:body :results 0 :regularMarketDayHigh])
     :baixa-dia   (get-in resposta [:body :results 0 :regularMarketDayLow])}))

(defn consultar-variacao-dia [ticker]
    (let [resposta (brapi/consulta ticker)
        variacao (get-in resposta [:body :results 0 :regularMarketChangePercent])
    ]
variacao))