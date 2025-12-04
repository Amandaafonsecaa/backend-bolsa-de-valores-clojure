(ns bolsa-de-valores.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.cors :refer [wrap-cors]]
            [bolsa-de-valores.routes :refer [app-routes]])
  (:gen-class))

(def app
  (-> app-routes
      (wrap-cors :access-control-allow-origin [#"http://localhost:3001"]
                 :access-control-allow-methods [:get :post :put :delete])          
      
      (wrap-json-body {:keywords? true})
      
      wrap-json-response))

(defn -main [& args]
  (run-jetty app {:port 3000 :join? false}))