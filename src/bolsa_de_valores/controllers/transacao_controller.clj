(ns bolsa-de-valores.controllers.transacao-controller
  (:require [bolsa-de-valores.services.transacao-service :as transacao-service]
            [bolsa-de-valores.services.carteira-service :as carteira-service]
            [ring.util.response :as resp]
            [bolsa-de-valores.services.cotacao-service :as cotacao-service]))


(defn comprar [request]
  (try
    (let [params (:body request)]
      (if (and (:ticker params) (:quantidade params))
        (let [transacao (transacao-service/comprar (:ticker params) (:quantidade params))]
          (-> (resp/response {:mensagem "Compra registrada com sucesso."
                             :transacao transacao})
              (resp/status 201)))
        (resp/bad-request {:erro "Parâmetros 'ticker' ou 'quantidade' ausentes."})))

    (catch Exception e
      (-> (resp/response {:erro "Erro ao processar a compra."
                          :detalhe (.getMessage e)})
          (resp/status 500)))))

(defn vender [request]
  (try
    (let [params (:body request)]
      (if (and (:ticker params) (:quantidade params))
        (let [transacao (transacao-service/vender (:ticker params) (:quantidade params))]
          (-> (resp/response {:mensagem "Venda registrada com sucesso."
                              :transacao transacao})
              (resp/status 201))) 
        (resp/bad-request {:erro "Parâmetros 'ticker' ou 'quantidade' ausentes."})))

    (catch Exception e
      (-> (resp/response {:erro "Erro ao processar a venda."
                          :detalhe (.getMessage e)})
          (resp/status 500)))))

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

(defn patrimonio [_]
  (try
    (resp/response {:patrimonio_liquido (carteira-service/saldo-total)})
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
