(ns leiningen.pallet-minisite
  "Pallet mini-site generator"
  (:require
   [leiningen.pallet-minisite.config :refer [minisite-config]]))

(defn pallet-minisite
  "I don't do a lot."
  [project & args]
  (minisite-config project))
