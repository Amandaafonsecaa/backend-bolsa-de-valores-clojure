(ns bolsa-de-valores.services.cotacao-service
  (:require [bolsa-de-valores.external.brapi-external :as brapi]))

(defn consultar-preco [ticker]
  (let [resposta (brapi/consulta ticker)
        preco    (get-in resposta [:body :results 0 :regularMarketPrice])]
    preco))

(defn consultar-detalhes [ticker] 
  (let [resposta (brapi/consulta ticker)]
    {:nome        (get-in resposta [:body :results 0 :longName])
     :nome-curto  (get-in resposta [:body :results 0 :shortName])
     :moeda       (get-in resposta [:body :results 0 :currency])
     :ultimo-preco (get-in resposta [:body :results 0 :regularMarketPrice])
     :preco-maximo (get-in resposta [:body :results 0 :regularMarketDayHigh])
     :preco-minimo (get-in resposta [:body :results 0 :regularMarketDayLow])
     :preco-abertura (get-in resposta [:body :results 0 :regularMarketOpen])
     :preco-fechamento (get-in resposta [:body :results 0 :regularMarketPreviousClose])
     :hora-cotacao (get-in resposta [:body :results 0 :regularMarketTime])}))

(defn consultar-variacao-dia [ticker]
    (let [resposta (brapi/consulta ticker)
          variacao  (get-in resposta [:body :results 0 :regularMarketChangePercent])]
      variacao))

(defn simular-compra [ticker qtd]
  (let [resposta      (brapi/consulta ticker)
        preco-unitario (get-in resposta [:body :results 0 :regularMarketPrice])
        preco-total    (* preco-unitario qtd)]
    preco-total))

(defn consultar-preco-na-data
  "Consulta o preço de `ticker` na data `data-str`.
   Tenta primeiro o preço de fechamento do dia; se não encontrar, cai pro preço atual."
  [ticker data-str]
  (let [resposta (brapi/consulta-historica ticker data-str)
        ;; algumas APIs históricas usam :close como preço de fechamento do dia
        preco-historico (or (get-in resposta [:body :results 0 :close])
                            (get-in resposta [:body :results 0 :regularMarketPrice]))]
    preco-historico))
