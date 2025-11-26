(ns bolsa-de-valores.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [bolsa-de-valores.routes :refer [app-routes]])
  (:gen-class))

;; Middleware + rotas
(def app
  (-> app-routes
      (wrap-json-body {:keywords? true}) ;; converte JSON em mapa com keywords
      wrap-json-response))               ;; transforma resposta em JSON

(defn -main [& args]
  (println "Servidor iniciado em http://localhost:3000")
  (run-jetty app {:port 3000 :join? false}))
