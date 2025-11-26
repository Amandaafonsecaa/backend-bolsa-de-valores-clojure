(ns bolsa-de-valores.controllers.transacao_controller
    (:require [bolsa-de-valores.services.transacao_service :as service]
            [ring.util.response :as resp]
    )
)

(defn comprar [request]
  (try
    (let [params (-> request :body)] 
      (if (and (:ticker params) (:quantidade params))
        (let [transacao (service/comprar (:ticker params) (:quantidade params))]
          (-> (resp/response {:mensagem "Compra registrada com sucesso." :transacao transacao})
              (resp/status 201))) 
        (resp/bad-request {:erro "Parâmetros 'ticker' ou 'quantidade' ausentes."})))

    (catch Exception e
      (resp/internal-server-error {:erro "Erro ao processar a compra." :detalhe (.getMessage e)}))))

(defn vender [request]
  (try
    (let [params (-> request :body)] 
      (if (and (:ticker params) (:quantidade params))
        (let [transacao (service/vender (:ticker params) (:quantidade params))]
          (-> (resp/response {:mensagem "Venda registrada com sucesso." :transacao transacao})
              (resp/status 201))) 
        (resp/bad-request {:erro "Parâmetros 'ticker' ou 'quantidade' ausentes."})))

    (catch Exception e
      (resp/internal-server-error {:erro "Erro ao processar a venda." :detalhe (.getMessage e)}))))

(defn extrato [_]
    (try
        (let [transacoes (service/extrato)]
            (resp/response transacoes)
        )
    catch Exception e
      (resp/internal-server-error {:erro "Erro ao buscar extrato." :detalhe (.getMessage e)})
      )
)