(ns bolsa-de-valores.routes
  (:require [compojure.core :refer [GET POST defroutes]]
            [bolsa-de-valores.controllers.transacao-controller :as transacao]
            [compojure.route :as route]))

(defroutes app-routes

  (POST "/transacoes/compra"  request (transacao/comprar request))
  (POST "/transacoes/venda"   request (transacao/vender request))

  (GET "/carteira/extrato" [] (transacao/extrato nil))

  (GET "/carteira/saldo" [] (transacao/saldo-ativo nil))
  (GET "/carteira/investido" [] (transacao/valor-investido nil))
  (GET "/carteira/lucro" [] (transacao/lucro-prejuizo nil))

  (GET  "/transacoes"         request (transacao/extrato request))

  (route/not-found "Rota não encontrada"))
