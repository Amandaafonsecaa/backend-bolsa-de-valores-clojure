(ns bolsa-de-valores.services.transacao_service
  (:require [bolsa-de-valores.services.cotacao_service :as cotacao]
            [bolsa-de-valores.repositories.transacao_repository :as repositorio]

  )
  )


(defn comprar [ticker quantidade]
  (let [preco-unitario (cotacao/consultar-preco ticker)
        total (* preco-unitario quantidade)
        data  (str (java.time.LocalDateTime/now))
        transacao {:ticker ticker
                   :tipo :compra
                   :quantidade quantidade
                   :preco preco-unitario
                   :total total
                   :data data}
                   ]
    (repositorio/adicionar! transacao)
    transacao)
    )

(defn vender [ticker quantidade]
  (let [preco-unitario (cotacao/consultar-preco ticker)
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

(defn extrato []
  (repositorio/listar)
)