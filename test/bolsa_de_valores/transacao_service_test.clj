(ns bolsa-de-valores.transacao-service-test
  (:require [clojure.test :refer :all]
            [bolsa-de-valores.services.transacao-service :as service]
            [bolsa-de-valores.services.carteira-service :as carteira]
            [bolsa-de-valores.repositories.transacao-repository :as repositorio]
            [bolsa-de-valores.services.cotacao-service :as cotacao]))

(deftest test-vender-mais-que-possui
  (testing "Tentativa de vender mais do que o saldo permite"
    (repositorio/limpar!)
    
    (with-redefs [cotacao/consultar-preco (fn [_] 10.0)]
      ;; 1. Compra 10
      (service/comprar "PETR4" 10)
      
      (is (= 10 (get (carteira/saldo-por-ativo) "PETR4" 0)) "Saldo deve ser 10 após compra")
      
      ;; 2. Tenta vender 20
      (try
        (service/vender "PETR4" 20)
        (is false "Deveria ter lançado uma exceção")
        (catch clojure.lang.ExceptionInfo e
          (is (= "Saldo insuficiente para esta venda." (.getMessage e)))
          (let [data (ex-data e)]
            (is (= 10 (:disponivel data)))
            (is (= 20 (:tentativa data))))))
      
      ;; 3. Verifica saldo e extrato
      (is (= 10 (get (carteira/saldo-por-ativo) "PETR4" 0)) "Saldo deve permanecer 10")
      (is (= 1 (count (repositorio/listar))) "Deve haver apenas 1 transação (a compra)"))))
