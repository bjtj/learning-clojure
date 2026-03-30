(ns app.core
  (:require [reagent.core :as r]
            [reagent.dom.client :as rdomc]
            [re-frame.core :as rf]))

(rf/reg-event-fx
 :app/start
 (fn [_ _]
   {:db {:count 0
         :time (js/Date.)
         :time-color "orange"}}))

;;; live
;;; https://day8.github.io/re-frame/dominoes-live/

(defn dispatch-timer-event []
  (let [now (js/Date.)]
    (rf/dispatch [:timer now])))

(defonce do-timer (js/setInterval dispatch-timer-event 1000))

(rf/reg-event-db
 :timer
 (fn [db [_ new-time]]
   (assoc db :time new-time)))

(rf/reg-event-db
 :time-color-change
 (fn [db [_ new-color-value]]
   (assoc db :time-color new-color-value)))

(rf/reg-sub
 :time
 (fn [db _]
   (:time db)))

(rf/reg-sub
 :time-color
 (fn [db _]
   (:time-color db)))

(defn clock []
  (let [colour @(rf/subscribe [:time-color])
        time (some-> @(rf/subscribe [:time])
                     .toTimeString
                     (clojure.string/split " ")
                     first)]
    [:div {:style {:font-size 50
                   :color colour}} time]))

(defn color-input []
  (let [gettext (fn [e] (.. e -target -value))
        emit (fn [e] (rf/dispatch [:time-color-change (gettext e)]))]
    [:div
     [:p "Display color:"]
     [:input {:type :text
              :style {:border "1px solid #CCC"}
              :value @(rf/subscribe [:time-color])
              :on-change emit}]]))

;;; counter

(rf/reg-event-fx
 :inc
 (fn [{:keys [db] :as coeffects} event]
   {:db (update db :count inc)}))

(rf/reg-sub
 :count
 (fn [db v]
   (:count db)))

(defn app []
  [:div
   [:h1 "Re-frame Example"]
   [:h2 "Counter"]
   [:p "count: " @(rf/subscribe [:count])]
   [:button
    {:on-click #(rf/dispatch [:inc])}
    "Increase"]
   [:div
    [:h2 "The time is now:"]
    [clock]
    [color-input]]])

(defonce root (delay (rdomc/create-root (.getElementById js/document "app"))))

(defn mount []
  (rdomc/render
   @root
   [app]))

(defn init []
  (mount)
  (rf/dispatch-sync [:app/start]))
