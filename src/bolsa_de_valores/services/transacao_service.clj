(ns bolsa-de-valores.services.transacao-service
  (:require [bolsa-de-valores.services.cotacao-service :as cotacao]
            [bolsa-de-valores.repositories.transacao-repository :as repositorio]
            [bolsa-de-valores.services.carteira-service :as carteira])
  (:import [java.time LocalDateTime]))

;; função privada !
(defn- formatar-data [data-str]
  (if data-str
    data-str
    (str (LocalDateTime/now))))

;; req. 2 !
(defn comprar [ticker quantidade data-str]
   (let [preco-unitario (cotacao/consultar-preco ticker) ;; preço atual da ação

        _ (when (nil? preco-unitario) 
                  (throw (ex-info "Cotação não encontrada. Verifique o ticker ou a disponibilidade da API externa."
                                    {:ticker ticker
                                    :erro "Preço unitário nulo"})))
         total (* preco-unitario quantidade) ;; calc. do total
         data (formatar-data data-str) ;; formatação
         transacao {:ticker ticker ;; mapa imutável 
                    :tipo :compra
                    :quantidade quantidade
                    :preco preco-unitario
                    :total total
                    :data data}]
     (repositorio/adicionar! transacao) ;; efeito colateral 
     transacao)) ;; retorna o mapa da transação

;; req. 3 !
(defn vender [ticker quantidade data-str]
  (let [data (formatar-data data-str)
        
        saldo-por-ativo-map (carteira/saldo-por-ativo data) ;; saldo da carteira
        saldo-atual (get saldo-por-ativo-map ticker 0)
        ;; _ -> resultado não vai ser usado (propósito principal é o efeito colateral)
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
    transacao)) ;; retorna