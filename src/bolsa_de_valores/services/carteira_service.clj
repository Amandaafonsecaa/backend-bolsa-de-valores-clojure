(ns bolsa-de-valores.services.carteira-service
  (:require [bolsa-de-valores.services.cotacao-service :as cotacao]
            [bolsa-de-valores.repositories.transacao-repository :as repositorio]))


(defn soma-saldo [transacoes]
  (reduce (fn [acc transacao]
            (let [tipo (get transacao :tipo)
                  quantidade (get transacao :quantidade)
                  q (or quantidade 0)]
              (if (= tipo :compra)
                (+ acc q)
                (- acc q))))
          0
          transacoes))

;; leitura e consultas

(defn extrato []
  (repositorio/listar))

(defn saldo-por-ativo []
  (let [transacoes (repositorio/listar)
        transacoes-validas (->> transacoes
                                (remove nil?)
                                (filter map?))
        agrupado-por-ticker (group-by :ticker transacoes-validas)]
    (into {} (map (fn [[ticker transacoes-do-ativo]]
                    [ticker (soma-saldo transacoes-do-ativo)])
                  agrupado-por-ticker))))

(def qtd-atual saldo-por-ativo) ;; alias

(defn valor-total-investido []
  (let [compras (filter #(= (:tipo %) :compra) (repositorio/listar))]
    (reduce + 0 (map :total compras))))

(defn lucro-ou-prejuizo []
  (let [saldos (saldo-por-ativo)
        valor-investido (valor-total-investido)
        valor-atual-total (reduce (fn [valor-acumulado [ticker qtd]]
                                    (if (pos? qtd)
                                      (let [preco-atual (cotacao/consultar-preco ticker)
                                            valor-ativo (* preco-atual qtd)]
                                        (+ valor-acumulado valor-ativo))
                                      valor-acumulado))
                                  0
                                  saldos)]
    (- valor-atual-total valor-investido)))
