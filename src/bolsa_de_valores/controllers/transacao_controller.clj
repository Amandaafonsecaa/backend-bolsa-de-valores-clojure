(ns bolsa-de-valores.controllers.transacao-controller
  (:require [bolsa-de-valores.services.transacao-service :as transacao-service]
            [bolsa-de-valores.services.carteira-service :as carteira-service]
            [ring.util.response :as resp]
            [bolsa-de-valores.services.cotacao-service :as cotacao-service]))

(defn comprar [request]
  (try
    (let [params (:body request)
          ticker (:ticker params) 
          quantidade (:quantidade params)
          data (:data params)]
      
      (if (and ticker quantidade data) 
        (let [transacao (transacao-service/comprar ticker quantidade data)]
          (-> (resp/response {:mensagem "Compra registrada com sucesso."
                             :transacao transacao}) 
              (resp/status 201)))
        (resp/bad-request {:erro "Parâmetros 'ticker', 'quantidade' ou 'data' ausentes."})))

    (catch Exception e
      (-> (resp/response {:erro "Erro ao processar a compra."
                      :detalhe (.getMessage e)})
          (resp/status 500)))))
(defn vender [request]
  (try
    (let [params (:body request)
          ticker (:ticker params)
          quantidade (:quantidade params)
          data (:data params)] 
      
      (if (and ticker quantidade data) 
        (let [transacao (transacao-service/vender ticker quantidade data)]
          (-> (resp/response {:mensagem "Venda registrada com sucesso."
                              :transacao transacao})
              (resp/status 201))) 
        (resp/bad-request {:erro "Parâmetros 'ticker', 'quantidade' ou 'data' ausentes."})))

    (catch Exception e
      (let [detalhes (ex-data e)]
        (if (= (:disponivel detalhes) 0) 
          (-> (resp/response {:erro "Saldo insuficiente para esta venda na data informada."
                              :detalhe (str "Disponível até " (:data-venda detalhes) ": " (:disponivel detalhes))})
              (resp/status 400))
          (-> (resp/response {:erro "Erro ao processar a venda."
                              :detalhe (.getMessage e)})
              (resp/status 500)))))))

(defn extrato [request] 
  (try
    (let [data-inicio (get-in request [:query-params :data_inicio]) 
          data-fim (get-in request [:query-params :data_fim])] 
      
      (if (or data-inicio data-fim)
        (resp/response (carteira-service/extrato data-inicio data-fim))
        (resp/response (carteira-service/extrato))))

    (catch Exception e
      (-> (resp/response {:erro "Erro ao buscar extrato."
                          :detalhe (.getMessage e)})
          (resp/status 500)))))
          
(defn saldo-ativo [_]
  (try
    (resp/response (carteira-service/saldo-por-ativo)) 
    (catch Exception e
      (-> (resp/response {:erro "Erro ao calcular saldo por ativo."
                          :detalhe (.getMessage e)})
          (resp/status 500)))))

(defn valor-investido [_]
  (try
    (resp/response {:valor_total_investido (carteira-service/valor-total-investido)})
    (catch Exception e
      (-> (resp/response {:erro "Erro ao calcular valor total investido."
                          :detalhe (.getMessage e)})
          (resp/status 500)))))

(defn patrimonio-liquido [_]
  (try
    (resp/response {:patrimonio_liquido (carteira-service/patrimonio-liquido)})
    (catch Exception e
      (-> (resp/response {:erro "Erro ao calcular patrimônio líquido."
                          :detalhe (.getMessage e)})
          (resp/status 500)))))

(defn consultar-dados-acao [req]
  (try
    (let [ticker (get-in req [:route-params :ticker])]
      (if ticker
        (let [detalhes (cotacao-service/consultar-detalhes ticker)]
          (resp/response detalhes))
        (resp/bad-request {:erro "o parâmetro ticker tá ausente"})))
    (catch Exception e 
      (-> (resp/response {:erro "erro ao buscar detalhes da ação"
                          :detalhe (.getMessage e)})
          (resp/status 500)))))

(defn lucro-prejuizo-total [_]
  (try
    (resp/response (carteira-service/lucro-prejuizo-total))
    (catch Exception e 
      (-> (resp/response {:erro "Erro ao calcular lucro/prejuizo"
                          :detalhe (.getMessage e)})
          (resp/status 500)))))

(defn saldo-total [_]
  (try
    (resp/response {:saldo_total (carteira-service/saldo-total)})
    (catch Exception e 
      (-> (resp/response {:erro "Erro ao calcular saldo total"
                          :detalhe (.getMessage e)})
          (resp/status 500)))))

(defn valor-total-investido[_]
  (try
    (resp/response {:valor_total_investido (carteira-service/valor-total-investido)})
    (catch Exception e 
      (-> (resp/response {:erro "Erro ao calcular valor total investido"
                          :detalhe (.getMessage e)})
          (resp/status 500)))))

(defn patrimonio-total [_]
  (try
    (resp/response {:patrimonio_total (carteira-service/patrimonio-total)})
    (catch Exception e
      (-> (resp/response {:erro "Erro ao calcular patrimônio total."
                          :detalhe (.getMessage e)})
          (resp/status 500)))))