(ns bolsa-de-valores.services.carteira-service
  (:require
   [bolsa-de-valores.repositories.transacao-repository :as repositorio]
   [bolsa-de-valores.services.cotacao-service :as cotacao]) 
  (:import
   [java.time LocalDateTime]))

;; conversão da data (de str para obj.)
(defn- str->datetime [date-str]
  (try
    (if date-str ;; foi fornecida?
      (LocalDateTime/parse date-str)
      nil) ;; se não, é nulo
    (catch Exception _ nil)))

(defn- filtrar-por-periodo [transacoes data-inicial-str data-final-str]
  (let [inicio (str->datetime data-inicial-str)
        fim (str->datetime data-final-str)]
    (filter (fn [transacao] ;; função de ordem superior
              (let [data-transacao (str->datetime (:data transacao))]
                (and data-transacao
                     (or (nil? inicio) ;; a data inicial é nula?
                         (not (.isBefore data-transacao inicio)))
                     (or (nil? fim) ;; a data final é nula?
                         (not (.isAfter data-transacao fim))))))
            transacoes)))

(defn soma-saldo [transacoes]
  (reduce (fn [acc transacao] ;; acumular saldo
            (let [tipo (get transacao :tipo) ;; compra ou venda?
                  quantidade (get transacao :quantidade) ;; qual a quantidade?
                  q (or quantidade 0)] ;; definindo a qtd.
              (if (= tipo :compra)
                (+ acc q) ;; adc. qtd. ao acc. (compra)
                (- acc q)))) ;; subtrai a qtd. o acumulador (venda)
          0 ;; valor inicial
          transacoes)) ; aplicando nessa lista

(defn extrato 
  ([] (repositorio/listar)) ;; aridade 1 -> lista completa de transações
  ([data-inicio-str data-fim-str] ;; aridade 2 -> com args. do período
   (let [transacoes (repositorio/listar)]
     (filtrar-por-periodo transacoes data-inicio-str data-fim-str))))

;; calcular a qtd. de ações por ativo
(defn saldo-por-ativo 
  ([] (saldo-por-ativo nil)) ;; aridade 1
  ([data-limite-str] ;; aridade 2
   (let [transacoes-brutas (repositorio/listar) 
         
         transacoes (if data-limite-str
                      (filtrar-por-periodo transacoes-brutas nil data-limite-str)
                      transacoes-brutas)
         
         transacoes-validas (->> transacoes
                                 (remove nil?)
                                 (filter map?)) ;; apenas os que são mapas
         agrupado-por-ticker (group-by :ticker transacoes-validas)] ;; agrupando por código
     
     (into {} (map (fn [[ticker transacoes-do-ativo]] ;; iterando
                     [ticker (soma-saldo transacoes-do-ativo)]) ;; calcular o saldo líquido 
                   agrupado-por-ticker))))) ;; transformando a lista de pares em um mapa

(defn valor-total-investido []
  (let [compras (filter #(= (:tipo %) :compra) (repositorio/listar))] ;; apenas do tipo compra
    (reduce + 0 (map :total compras))))

;; req. 5
(defn saldo-total []
  (let [saldos (saldo-por-ativo)]
    (reduce (fn [valor-acc [ticker qtd]] ;; somar o valor de mercado de cada ativo
              (if (pos? qtd) ;; é positivo?
                (let [preco-atual (cotacao/consultar-preco ticker)
                      valor-ativo (* preco-atual qtd)]
                  (+ valor-acc valor-ativo))
                valor-acc)) ;; se for zero ou neg, mantêm o acc
            0
            saldos)))