(ns bolsa-de-valores.repositories.transacao-repository)

(def transacoes (atom []))


(defn adicionar![transacao]
    (swap! transacoes conj transacao)
)

(defn listar[]
    @transacoes
)

(defn listar-por-ticker [ticker]
    (filter (fn[transacao]
                (= (:ticker transacao) ticker)
    ) @transacoes)
)

(defn remover! [ticker]
  (swap! transacoes (fn [lista]
                      (remove (fn [transacao]
                                (= (:ticker transacao) ticker)) lista))))

(defn limpar! []
    (reset! transacoes [])
)
