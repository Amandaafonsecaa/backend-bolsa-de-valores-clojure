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

  (GET "/carteira/saldo" [] (transacao/saldo-ativo nil))
  (GET "/carteira/investido" [] (transacao/valor-investido nil))
  (GET "/carteira/patrimonio" [] (transacao/patrimonio nil))

  (route/not-found "Rota não encontrada"))
