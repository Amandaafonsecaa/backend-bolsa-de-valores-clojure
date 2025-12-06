(ns bolsa-de-valores.routes
  (:require [compojure.core :refer [GET POST defroutes]]
            [bolsa-de-valores.services.cotacao-service :as cotacao]
            [bolsa-de-valores.controllers.transacao-controller :as transacao]
            [ring.util.response :refer [response]]
            [compojure.route :as route]))

(defroutes app-routes

  (POST "/transacoes/compra"  request (transacao/comprar request))
  (POST "/transacoes/venda"   request (transacao/vender request))

  (GET "/carteira/extrato" request (transacao/extrato request))

  (GET "/cotacao/:ticker" [ticker] 
    (response (cotacao/consultar-detalhes ticker)))
  
  (GET "/carteira/saldo" [] (transacao/saldo-ativo nil)) ; QTD DE ACOES Q ELE TEM
  (GET "/carteira/saldototal" [] (transacao/saldo-total nil)) 
  ; saldo total -> valor em reais que é o valor de todas as ações que ele possui
  (GET "/carteira/investido" [] (transacao/valor-investido nil))
  (GET "/transacoes/patrimonio-total" [] (transacao/patrimonio-total nil))
  (GET "/transacoes/patrimonio-liquido" [] (transacao/patrimonio-liquido nil))
  (GET "/carteira/lucroprejuizo" [] (transacao/lucro-prejuizo-total nil))
  (GET "/carteira/valor-total-investido" [] (transacao/valor-total-investido nil)) ; Saldo (todo dinheiro ja gasto para comprar acao)

  (route/not-found "Rota não encontrada"))