(ns bolsa-de-valores.repositories.transacao_repository)

(def transacoes (atom []))


(defn adicionar![transacao]
    (swap! transacoes conj transacao)
)

(defn listar[]
    @transacoes
)

(defn remover[ticker]
    (swap! transacoes (fn[lista]
        (filter #(not= (:ticker %) ticker) lista)
    ))
)