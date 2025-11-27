(ns bolsa-de-valores.controllers.transacao-controller
  (:require [bolsa-de-valores.services.transacao-service :as transacao-service]
            [bolsa-de-valores.services.carteira-service :as carteira-service]
            [ring.util.response :as resp]))


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


(defn extrato [_]
  (try
    (resp/response (carteira-service/extrato))

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

(defn lucro-prejuizo [_]
  (try
    (resp/response {:lucro_ou_prejuizo (carteira-service/lucro-ou-prejuizo)})
    (catch Exception e
      (-> (resp/response {:erro "Erro ao calcular lucro ou prejuízo."
                          :detalhe (.getMessage e)})
          (resp/status 500)))))
