(defproject bolsa-de-valores "0.1.0-SNAPSHOT"
  :description "Backend da bolsa de valores em Clojure"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.cli "1.0.206"]

                 ;; Servidor HTTP (Ring)
                 [ring/ring-core "1.9.0"]
                 [ring/ring-jetty-adapter "1.9.0"]

                 ;; Rotas (Compojure)
                 [compojure "1.6.2"]

                 ;; JSON (Cheshire) - usado para encode/decode manual
                 [cheshire "5.10.0"]

                 ;; JSON automático com wrappers
                 [ring/ring-json "0.5.1"]

                 ;; HTTP Client para conectar ao BRAPI
                 [clj-http "3.12.3"]]
  
  :main ^:skip-aot bolsa-de-valores.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
