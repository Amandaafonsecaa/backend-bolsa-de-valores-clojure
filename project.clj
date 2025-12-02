(defproject bolsa-de-valores "0.1.0-SNAPSHOT"
  :description "Backend da bolsa de valores em Clojure"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later-with-Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.cli "1.0.206"]
                 [ring/ring-core "1.9.0"]
                 [ring/ring-jetty-adapter "1.9.0"]
                 [compojure "1.6.2"]
                 [cheshire "5.10.0"]
                 [ring/ring-json "0.5.1"]
                 [clj-http "3.12.3"]
                 
                 ;; Variáveis de ambiente
                 [environ "1.2.0"]

                 ;; middleware
                 [ring-cors "0.1.13"]]

  :plugins [[lein-environ "1.2.0"]]
  
  :main ^:skip-aot bolsa-de-valores.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})