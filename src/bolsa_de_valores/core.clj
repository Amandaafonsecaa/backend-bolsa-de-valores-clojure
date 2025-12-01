(ns bolsa-de-valores.core
  (:require [ring.adapter.jetty :as jetty]
            [bolsa-de-valores.routes :refer [app-routes]] ;; Suas rotas importadas
            
            ;; Importar os Middlewares
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.cors :refer [wrap-cors]]) ;; <--- Importante
  (:gen-class))

;; Criamos a aplicação com os "poderes" (middlewares)
(def app
  (-> app-routes
      ;; 1. Transforma o corpo da requisição em JSON
      (wrap-json-body {:keywords? true})
      
      ;; 2. Transforma a resposta em JSON pro Front entender
      (wrap-json-response)
      
      ;; 3. LIBERA O FRONTEND (CORS)
      (wrap-cors :access-control-allow-origin [#".*"]  ;; Aceita tudo (modo dev)
                 :access-control-allow-methods [:get :put :post :delete])))

(defn -main [& args]
  ;; Rodando na porta 3000
  (jetty/run-jetty app {:port 3000 :join? false}))