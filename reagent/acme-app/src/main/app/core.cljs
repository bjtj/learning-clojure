(ns app.core
  (:require [reagent.core :as r]
            [reagent.dom.client :as rdomc]))

(defonce counter (r/atom 0))

(defn app []
  [:div
   [:h1 "Reagent Example"]
   [:p "count: " @counter]
   [:button
    {:on-click #(swap! counter inc)}
    "Increase"]])

(defonce root (delay (rdomc/create-root (.getElementById js/document "app"))))

(defn mount []
  (rdomc/render
   @root
   [app]))

(defn init []
  (mount))

(comment
  (js/console.log "hi")
  (js/alert "wow")
  ;; comment
  )
