(ns bolsa-de-valores.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [bolsa-de-valores.routes :refer [app-routes]])
  (:gen-class))

(def app
  (-> app-routes
      (wrap-json-body {:keywords? true}) 
      wrap-json-response))               

(defn -main [& args]
  (println "Servidor iniciado em http://localhost:3000")
  (run-jetty app {:port 3000 :join? false}))
