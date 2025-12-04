(ns bolsa-de-valores.repositories.transacao-repository)
;; camaada de persistência em memória 

;; o atom gerencia o estado mutável de forma segura
(def transacoes (atom []))

;; o ! é pra falar que vai causar um efeito colateral (altera o estado do atom)
(defn adicionar![transacao]
    (swap! transacoes conj transacao)  ;; adc. no final da lista
)

(defn listar[]
    @transacoes  ;; @ (deref) é pra pegar o valor atual do atom 
)

(defn listar-por-ticker [ticker] 
    (filter (fn[transacao]
                (= (:ticker transacao) ticker) ;; ticker da transação == ticker do param ??? 
    ) @transacoes)
)

(defn remover! [ticker]
  (swap! transacoes (fn [lista] ;; atualiza o atom transações
                      (remove (fn [transacao]
                                (= (:ticker transacao) ticker)) lista))))

(defn limpar! []
    (reset! transacoes [])
)
