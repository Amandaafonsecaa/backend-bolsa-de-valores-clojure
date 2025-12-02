(ns bolsa-de-valores.core
  (:require [ring.adapter.jetty :refer [run-jetty]] ;; Importou a função direta
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.cors :refer [wrap-cors]]
            [bolsa-de-valores.routes :refer [app-routes]])
  (:gen-class))

(def app
  (-> app-routes
      ;; Configuração do CORS (Permitindo o Front na porta 3001)
      (wrap-cors :access-control-allow-origin [#"http://localhost:3001"] 
                 :access-control-allow-methods [:get :post :put :delete])          
      
      ;; Lida com JSON na entrada (body)
      (wrap-json-body {:keywords? true})
      
      ;; Lida com JSON na saída (response)
      wrap-json-response))

(defn -main [& args]
  ;; CORREÇÃO: Removi o "jetty/" antes do run-jetty
  (run-jetty app {:port 3000 :join? false}))