(ns bolsa-de-valores.services.transacao-service
  (:require [bolsa-de-valores.services.cotacao-service :as cotacao]
            [bolsa-de-valores.repositories.transacao-repository :as repositorio]
            [bolsa-de-valores.services.carteira-service :as carteira])
  (:import [java.time LocalDateTime]))

(defn- formatar-data [data-str]
  (if data-str
    data-str
    (str (LocalDateTime/now))))

(defn comprar [ticker quantidade data-str]
   (let [preco-unitario (cotacao/consultar-preco ticker)
         total (* preco-unitario quantidade)
         data (formatar-data data-str)
         transacao {:ticker ticker
                    :tipo :compra
                    :quantidade quantidade
                    :preco preco-unitario
                    :total total
                    :data data}]
     (repositorio/adicionar! transacao)
     transacao))


(defn vender [ticker quantidade data-str]
  (let [data (formatar-data data-str)
        
        saldo-por-ativo-map (carteira/saldo-por-ativo data) 
        saldo-atual (get saldo-por-ativo-map ticker 0)
        
        _ (when (> quantidade saldo-atual)
            (throw (ex-info "Saldo insuficiente para esta venda na data informada."
                            {:ticker ticker
                             :data-venda data
                             :tentativa quantidade
                             :disponivel saldo-atual})))
        
        preco-unitario (cotacao/consultar-preco ticker)
        total (* preco-unitario quantidade)
        
        transacao {:ticker ticker
                   :tipo :venda
                   :quantidade quantidade
                   :preco preco-unitario
                   :total total
                   :data data}]
    (repositorio/adicionar! transacao)
    transacao))