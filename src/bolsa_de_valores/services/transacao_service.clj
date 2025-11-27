(ns bolsa-de-valores.services.transacao-service
  (:require [bolsa-de-valores.services.cotacao-service :as cotacao]
            [bolsa-de-valores.repositories.transacao-repository :as repositorio]
            [bolsa-de-valores.services.carteira-service :as carteira]))


(defn comprar [ticker quantidade]
   (let [preco-unitario (cotacao/consultar-preco ticker)
         total (* preco-unitario quantidade)
         data  (str (java.time.LocalDateTime/now))
         transacao {:ticker ticker
                    :tipo :compra
                    :quantidade quantidade
                    :preco preco-unitario
                    :total total
                    :data data}]
     (repositorio/adicionar! transacao)
    transacao)
    )


(defn vender [ticker quantidade]
  (let [saldo-por-ativo-map (carteira/saldo-por-ativo)
        saldo-atual (get saldo-por-ativo-map ticker 0)
        
        _ (when (> quantidade saldo-atual)
            (throw (ex-info "Saldo insuficiente para esta venda."
                            {:ticker ticker
                             :tentativa quantidade
                             :disponivel saldo-atual})))
        
        preco-unitario (cotacao/consultar-preco ticker)
        total (* preco-unitario quantidade)
        data  (str (java.time.LocalDateTime/now))
        transacao {:ticker ticker
                   :tipo :venda
                   :quantidade quantidade
                   :preco preco-unitario
                   :total total
                   :data data}]
    (repositorio/adicionar! transacao)
    transacao))
