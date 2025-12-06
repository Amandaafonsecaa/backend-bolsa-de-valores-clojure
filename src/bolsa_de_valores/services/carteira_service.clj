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
  ; somar o total gasto em compras, se comprar 100 acoes cada uma a 10 reais , total investido = 1000, se vender nao diminui o valor investido
  (let [compras (filter #(= (:tipo %) :compra) (repositorio/listar))] 
    (reduce + 0M (map :total compras))))

(defn valor-total-vendido []
  ; somar o total ganho em vendas, se comprar 100 acoes cada uma a 10 reais , total investido = 1000, se vender nao diminui o valor investido
  (let [vendas (filter #(= (:tipo %) :venda) (repositorio/listar))] 
    (reduce + 0 (map :total vendas))))


(defn saldo-total []
  (let [saldos (saldo-por-ativo)]
    (reduce (fn [valor-acc [ticker qtd]]
              (if (pos? qtd) 
                (let [;; Tenta obter a cotação. Se falhar ou for nil/0, trata como 0M.
                      preco-bruto (cotacao/consultar-preco ticker)
                      
                      ;; Garante que o preco é BigDecimal antes de qualquer multiplicação
                      preco-atual (if (some? preco-bruto) (bigdec preco-bruto) 0M)
                      
                      ;; A quantidade também deve ser BigDecimal para o cálculo correto
                      qtd-bigdec (bigdec qtd)
                      
                      valor-ativo (* preco-atual qtd-bigdec)]
                  
                  (+ valor-acc valor-ativo))
                valor-acc)) 
            0M ; <-- Inicializa o acumulador como BigDecimal (0M)
            saldos)))

(defn patrimonio-total []
  (let [dinheiro-venda (valor-total-vendido)
        saldo-total (saldo-total)
        _ (println (str "DEBUG - Saldo Total: " saldo-total))
        _ (println (str "DEBUG - Dinheiro Venda: " dinheiro-venda))
        patrimonio-atual (+ saldo-total dinheiro-venda)]
        patrimonio-atual))

(defn patrimonio-liquido []
  (let [patrimonio (patrimonio-total)
        _ (println (str "DEBUG - Patrimonio Total: " patrimonio))
        valor-investido (valor-total-investido)
        _ (println (str "DEBUG - Valor Total Investido: " valor-investido))
        patrimonio-liquido (- patrimonio valor-investido)]
    patrimonio-liquido))

(defn lucro-prejuizo-total []
  (let [valor-atual (bigdec (saldo-total))       ; Garante que VA é BigDecimal
        dinheiro-vendas (bigdec (valor-total-vendido)) ; Garante que DV é BigDecimal
        valor-gasto (bigdec (valor-total-investido))    ; Garante que VG é BigDecimal
        
        _ (println (str "DEBUG - Valor Atual (VA): " valor-atual))
        _ (println (str "DEBUG - Dinheiro Vendas (DV): " dinheiro-vendas))
        _ (println (str "DEBUG - Valor Gasto (VG): " valor-gasto))
        
        patrimonio-total (+ valor-atual dinheiro-vendas)
        
        _ (println (str "DEBUG - Patrimonio Total (VA+DV): " patrimonio-total))
        
        diferenca (- patrimonio-total valor-gasto)
        
        _ (println (str "DEBUG - Diferenca (P&L): " diferenca))
        
        eh-lucro? (pos? diferenca)
        
        percentual-bruto (if (pos? valor-gasto)
                           (* 100M (/ diferenca valor-gasto)) ; Use 100M para forçar o cálculo BigDecimal
                           0M)
        
        _ (println (str "DEBUG - Percentual Bruto (%): " percentual-bruto))]
    
    {:valor-reais (double diferenca) 
     :percentual (double percentual-bruto) 
     :eh-lucro? eh-lucro?}))