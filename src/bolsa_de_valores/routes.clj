(ns bolsa-de-valores.routes
  (:require [compojure.core :refer :all]
            [bolsa-de-valores.controllers.transacao_controller :as transacao]
            [compojure.route :as route]
            ))

(defroutes app-routes

  (POST "/transacoes/compra"  request (transacao/comprar request))
  (POST "/transacoes/venda"   request (transacao/vender request))
  (GET  "/transacoes"         request (transacao/extrato request))

  (route/not-found "Rota não encontrada"))
