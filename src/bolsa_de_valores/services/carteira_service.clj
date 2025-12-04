(ns bolsa-de-valores.services.carteira-service
  (:require
   [bolsa-de-valores.repositories.transacao-repository :as repositorio]
   [bolsa-de-valores.services.cotacao-service :as cotacao]) 
  (:import
   [java.time LocalDateTime]))

(defn- str->datetime [date-str]
  (try
    (if date-str
      (LocalDateTime/parse date-str)
      nil)
    (catch Exception _ nil)))

(defn- filtrar-por-periodo [transacoes data-inicial-str data-final-str]
  (let [inicio (str->datetime data-inicial-str)
        fim (str->datetime data-final-str)]
    (filter (fn [transacao]
              (let [data-transacao (str->datetime (:data transacao))]
                (and data-transacao
                     (or (nil? inicio)
                         (not (.isBefore data-transacao inicio)))
                     (or (nil? fim)
                         (not (.isAfter data-transacao fim))))))
            transacoes)))

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

(defn extrato 
  ([] (repositorio/listar))
  ([data-inicio-str data-fim-str]
   (let [transacoes (repositorio/listar)]
     (filtrar-por-periodo transacoes data-inicio-str data-fim-str))))

(defn saldo-por-ativo 
  ([] (saldo-por-ativo nil))
  ([data-limite-str]
   (let [transacoes-brutas (repositorio/listar) 
         
         transacoes (if data-limite-str
                      (filtrar-por-periodo transacoes-brutas nil data-limite-str)
                      transacoes-brutas)
         
         transacoes-validas (->> transacoes
                                 (remove nil?)
                                 (filter map?))
         agrupado-por-ticker (group-by :ticker transacoes-validas)]
     
     (into {} (map (fn [[ticker transacoes-do-ativo]]
                     [ticker (soma-saldo transacoes-do-ativo)])
                   agrupado-por-ticker)))))

(defn valor-total-investido []
  (let [compras (filter #(= (:tipo %) :compra) (repositorio/listar))]
    (reduce + 0 (map :total compras))))

(defn saldo-total []
  (let [saldos (saldo-por-ativo)]
    (reduce (fn [valor-acc [ticker qtd]]
              (if (pos? qtd)
                (let [preco-atual (cotacao/consultar-preco ticker)
                      valor-ativo (* preco-atual qtd)]
                  (+ valor-acc valor-ativo))
                valor-acc))
            0
            saldos)))