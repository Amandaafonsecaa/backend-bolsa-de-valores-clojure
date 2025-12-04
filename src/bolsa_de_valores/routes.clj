(ns bolsa-de-valores.routes
  (:require [compojure.core :refer [GET POST defroutes]] ;; defroutes agrupa rotas
            [bolsa-de-valores.controllers.transacao-controller :as transacao]
            [compojure.route :as route])) ;; rotas de utilidade (pag. 404)

(defroutes app-routes

  ;; o request vai desempacotar dados

  (POST "/transacoes/compra"  request (transacao/comprar request))
  (POST "/transacoes/venda"   request (transacao/vender request))

  (GET "/carteira/extrato" request (transacao/extrato request))

   (GET "/cotacao/:ticker" [ticker] 
    (response (cotacao/consultar-detalhes ticker)))

  (GET "/carteira/saldo" [] (transacao/saldo-ativo nil))
  (GET "/carteira/investido" [] (transacao/valor-investido nil))
  (GET "/carteira/patrimonio" [] (transacao/patrimonio nil))

  (route/not-found "Rota não encontrada"))
