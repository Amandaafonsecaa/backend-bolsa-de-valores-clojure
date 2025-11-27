(ns bolsa-de-valores.config
  (:require [environ.core :refer [env]]))

(def brapi-api-url (env :brapi-api-url "https://brapi.dev/api/quote/"))
