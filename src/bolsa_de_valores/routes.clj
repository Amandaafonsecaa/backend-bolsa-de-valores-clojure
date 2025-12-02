(ns bolsa-de-valores.routes
  (:require [compojure.core :refer [GET POST defroutes]]
            [bolsa-de-valores.controllers.transacao-controller :as transacao]
            [compojure.route :as route]))

(defroutes app-routes

  (POST "/transacoes/compra"  request (transacao/comprar request))
  (POST "/transacoes/venda"   request (transacao/vender request))

  (GET "/carteira/extrato" request (transacao/extrato request))

  (GET "/cotacao/:ticker" request (transacao/consultar-dados-acao request))

  (GET "/carteira/saldo" [] (transacao/saldo-ativo nil))
  (GET "/carteira/investido" [] (transacao/valor-investido nil))
  (GET "/carteira/patrimonio" [] (transacao/patrimonio nil))

  ;; (GET  "/transacoes"         request (transacao/extrato request))
  
  (route/not-found "Rota não encontrada"))
