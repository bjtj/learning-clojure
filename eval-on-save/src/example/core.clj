(ns example.core
  (:require [example.reload :as reload]
            [nextjournal.beholder :as beholder]
            [example.webserver :as web]
            [example.nrepl :as nrepl]))

(defonce watcher (atom nil))
(defonce system (atom nil))

(defn index [req]
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body "Hello"})

(def handler
  (web/ring-handler {:router [["/"   {:get index}]]}))

(defn start-webserver []
  (let [new-system (web/create-system
                    {:cookie-secret (web/gen-cookie-secret)
                     :handler #'handler})]
    (reset! system (web/start new-system))))

(defn restart-webserver []
  (when-let [sys @system]
    (web/stop sys)
    (reset! system nil))
  (start-webserver))

(defn watch [dirs]
  (reset! watcher (apply beholder/watch
                         (fn [{:keys [path]}]
                           (prn :modified path)
                           (swap! reload/global-tracker reload/refresh dirs))
                         dirs)))

(defn -main [& args]
  (watch ["src"])
  (start-webserver)
  (nrepl/run-nrepl))
