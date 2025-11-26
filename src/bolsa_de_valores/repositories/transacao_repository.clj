(ns bolsa-de-valores.repositories.transacao_repository)

(def transacoes (atom []))


(defn adicionar![transacao]
    (swap! transacoes conj transacao)
)

(defn listar[]
    @transacoes
)

(defn listar-por-ticker [ticker]
    (swap! transacoes (fn[lista]
                        (filter (fn[transacao]
                                    (= :ticker transacao ticker)
                        ) transacoes)
    ))
)

(defn remover[ticker]
    (filter (fn [transacao]
            (= (:ticker transacao) ticker))
          @transacoes))

(defn limpar! []
    (reset! transacoes [])
)