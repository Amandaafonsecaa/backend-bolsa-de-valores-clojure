(defproject bolsa-de-valores "0.1.0-SNAPSHOT"
  :description "Projeto de análise de bolsa de valores"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [ring/ring-core "1.9.5"]
                 [ring/ring-jetty-adapter "1.9.5"]
                 [compojure "1.7.0"]
                 [cheshire "5.10.2"]]
  :main ^:skip-aot bolsa-de-valores.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
