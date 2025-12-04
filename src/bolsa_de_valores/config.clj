(ns bolsa-de-valores.config
  (:require [environ.core :refer [env]])) ;; usada para ler var. de ambiente

;; url da api de cotações da bolsa de valores
(def brapi-api-url (env :brapi-api-url "https://brapi.dev/api/quote/"))
