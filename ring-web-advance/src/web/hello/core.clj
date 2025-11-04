(ns web.hello.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.session.cookie :as cookie]
            [ring.util.response :as res]
            [clojure.pprint :refer [pprint]]
            [hiccup2.core :as h]))

(defn hello [_]
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body (-> [:div
              [:h1 "Hello"]
              [:ul
               [:li [:a {:href "/say"} "say"]]
               [:li [:a {:href "/info"} "info"]]]]
             h/html
             str)})

(defn info [req]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (with-out-str (pprint req))})

(defn say [{:keys [session]}]
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body (str (h/html [:div
                       (when-some [greeting (:greeting session)]
                         [:p "Greeting: " greeting])
                       [:form {:action "/say" :method "post"}
                        [:input {:name "greeting" :placeholder "Say something..."}]
                        [:input {:type "submit" :value "Say"}]]]))})

(defn say-hello-handler [{:keys [request-method params session]}]
  (when-not (= request-method :post)
    (throw (Exception. "Not allowed method")))
  (when-not (get params "greeting")
    (throw (Exception. "greeting parameter is needed.")))
  (-> (res/redirect "/say" 303)
      (assoc :session (assoc session :greeting (get params "greeting")))))

(defn router [{:keys [uri request-method] :as req}]
  (case uri
    "/" (hello req)
    "/info" (info req)
    "/say" (case request-method
             :get (say req)
             :post (say-hello-handler req))))

(defn wrap-server [handler]
  (fn [request]
    (assoc-in (handler request) [:headers "Server"] "My Web Server(1.0)")))

(defonce server (atom nil))

(defn -wrap-session [handler]
  (wrap-session handler {:cookie-attrs {:max-age (* 60 60 24 60)
                                        :same-site :lax
                                        :http-only true}}))

(def app
  (-> router
      wrap-params
      -wrap-session
      wrap-server))

(defn- start-server []
  (reset! server (run-jetty (fn [req] (app req)) {:port 3000 :join? false})))

(defn- restart-server []
  (when @server
    (.stop @server)
    (start-server)))

(defn run-nrepl []
  (try
    (when-let [nrepl-main (requiring-resolve 'nrepl.cmdline/-main)]
      (nrepl-main "--port" "8888" "--middleware" "[cider.nrepl/cider-middleware]"))
    (catch Exception e
      (prn :error (.getMessage e)))))

(defn -main []
  (start-server)
  (run-nrepl))
