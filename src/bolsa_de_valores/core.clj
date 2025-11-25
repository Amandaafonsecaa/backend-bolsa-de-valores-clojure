(ns bolsa-de-valores.core
  (:require	[clojure.tools.cli	:refer	[parse-opts]])
	(:gen-class))

(defn- tem-a [x]
  (if (.startsWith x "A") 
    (println "Tem A")
    (println "Não tem")
  )
)

; - na função main quer dizer que ela é estática por conta da interoperabilidade do java

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ; o & quer dizer que o num de args é indefinido 
  (println "o nome " (map tem-a args)))
  
