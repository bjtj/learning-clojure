(ns hello-world.core
  (:require [reitit.core :as r]
            [reitit.ring :as ring]
            [ring.adapter.jetty :refer [run-jetty]]))

(defonce server (atom nil))

(defn index
  "index route"
  [_]
  {:status 200, :body "> (app {:request-method :get :uri \"/api/ping\"})
;;{:status 200, :body \"ok\", :wrap (:api)}
> (app {:request-method :get :uri \"/api/admin/users\"})
;;{:status 200, :body \"ok\", :wrap (:api :admin)}
> (-> app (ring/get-router) (r/match-by-name ::ping))
;;{:template \"/api/ping\",
;; :data
;; {:middleware [[#function[hello-world.core/wrap] :api]],
;;  :get {:handler #function[hello-world.core/handler]},
;;  :name :hello-world.core/ping},
;; :result
;; {:get
;;  {:data
;;   {:middleware [[#function[hello-world.core/wrap] :api]],
;;    :name :hello-world.core/ping,
;;    :handler #function[hello-world.core/handler]},
;;   :handler #function[hello-world.core/wrap/fn--13878],
;;   :path \"/api/ping\",
;;   :method :get,
;;   :middleware
;;   [{:name nil,
;;     :wrap #function[reitit.middleware/eval2100/fn--2102/fn--2107],
;;     :spec nil}]},
;;  :head nil,
;;  :post nil,
;;  :put nil,
;;  :delete nil,
;;  :connect nil,
;;  :options
;;  {:data
;;   {:middleware [[#function[hello-world.core/wrap] :api]],
;;    :name :hello-world.core/ping,
;;    :no-doc true,
;;    :handler #function[reitit.ring/fn--2292/fn--2301]},
;;   :handler #function[hello-world.core/wrap/fn--13878],
;;   :path \"/api/ping\",
;;   :method :options,
;;   :middleware
;;   [{:name nil,
;;     :wrap #function[reitit.middleware/eval2100/fn--2102/fn--2107],
;;     :spec nil}]},
;;  :trace nil,
;;  :patch nil},
;; :path-params nil,
;; :path \"/api/ping\"}"})

(defn handler
  "sample handler"
  [_]
  {:status 200, :body "ok"})

(defn wrap
  "middleware :: wrap"
  [handler id]
  (fn [request]
    (update (handler request) :wrap (fnil conj '()) id)))

(def app
  (ring/ring-handler
   (ring/router
    [["/" {:get index}]
     ["/api" {:middleware [[wrap :api]]}
      ["/ping" {:get handler
                :name ::ping}]
      ["/admin" {:middleware [[wrap :admin]]}
       ["/users" {:get handler
                  :post handler}]]]])))

(defn start
  ""
  []
  (if (some? @server)
    (println "already started stop first to start again")
    (do 
      (reset! server (run-jetty app {:port 3000 :join? false}))
      (println "server running in port 3000"))))

(defn stop
  ""
  []
  (when (some? @server)
    (.stop @server)
    (reset! server nil)))

(defn restart
  ""
  []
  (stop)
  (start))

(defn -main [& args]
  (start))
