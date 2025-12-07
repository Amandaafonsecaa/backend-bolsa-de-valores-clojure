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
  ([pagina limite data-inicio-str data-fim-str]
   (let [todas-transacoes (repositorio/listar)
         transacoes-filtradas (filtrar-por-periodo todas-transacoes data-inicio-str data-fim-str)
         
         limite (or limite 10)
         pagina (or pagina 1)
         
         todas-transacoes-filtradas-revertidas (reverse transacoes-filtradas)
         
         offset (* (dec pagina) limite)
         
         transacoes-paginadas (->> todas-transacoes-filtradas-revertidas
                                   (drop offset)
                                   (take limite))
         
         total-itens (count transacoes-filtradas)
         total-paginas (int (Math/ceil (/ total-itens limite)))]
     
     {:transacoes transacoes-paginadas
      :total-itens total-itens
      :total-paginas total-paginas}))
  
  ([data-inicio-str data-fim-str]
   (extrato 1 10 data-inicio-str data-fim-str))
  
  ([]
   (extrato 1 10 nil nil)))

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
    (reduce + 0M (map :total compras))))

(defn valor-total-vendido []
  (let [vendas (filter #(= (:tipo %) :venda) (repositorio/listar))] 
    (reduce + 0M (map :total vendas))))


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
  (let [valor-atual  (saldo-total)       ; Garante que VA é BigDecimal
        dinheiro-vendas (valor-total-vendido) ; Garante que DV é BigDecimal
        valor-gasto (valor-total-investido)   ; Garante que VG é BigDecimal
        
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
