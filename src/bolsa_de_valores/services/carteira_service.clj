(ns bolsa-de-valores.services.carteira_service
  (:require [bolsa-de-valores.services.cotacao_service :as cotacao]
            [bolsa-de-valores.repositories.transacao_repository :as repositorio]

  )
  )

(defn extrato []
  (repositorio/listar)
)

(defn qtd-ticker []
  (let [transacoes (repositorio/listar)]
    (count
     (filter (fn [transacao]
               (not= (:tipo transacao) :venda))
             transacoes))))
