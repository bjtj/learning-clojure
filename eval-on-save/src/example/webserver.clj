(ns example.webserver
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.session.cookie :as cookie]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :as res]
            [reitit.ring :as ring]
            [hiccup2.core :as h]
            [example.util :as util]))

(defn html [opts & elems]
  (let [{:keys [title]} opts]
    (-> (h/html
            (h/raw "<!DOCTYPE html>")
            [:html {:lang "en"}
             [:head
              [:title (or title "Hello")]
              [:meta {:charset "utf-8"}]
              [:link {:rel "stylesheet" :href "style.css"}]]
             [:body elems]])
        str)))

(defn gen-cookie-secret []
  "https://ring-clojure.github.io/ring/ring.middleware.session.cookie.html
  > :key - The secret key to encrypt the session cookie. Must be a byte array of
  >     exactly 16 bytes."
  (let [buffer (byte-array 16)]
    (.nextBytes (java.security.SecureRandom/getInstanceStrong) buffer)
    buffer))

(defn wrap-internal-error
  [handler & {:keys [ui]
              :or {ui (fn [t]
                        {:status 500
                         :headers {"Content-Type" "text/html; charset=utf-8"}
                         :body (html {}
                                     [:h1 "Internal Error"]
                                     [:p [:span "message: "] [:code (.getMessage t)]])})}}]
  (fn [req]
    (try 
      (handler req)
      (catch Throwable t
        (ui t)))))

(defn -wrap-session [handler {:keys [cookie-secret]}]
  (let [cookie-secret cookie-secret]
    (wrap-session handler {:cookie-attrs {:max-age (* 60 60 24 60)
                                          :same-site :lax
                                          :http-only true}
                           :store (cookie/cookie-store {:key cookie-secret})})))

(defn ring-handler [{:keys [router resource-path cookie-secret on-error on-internal-error]
                     :or {resource-path "public"
                          on-error (fn [status]
                                     (case status
                                       404 {:status 404 :body "not found"}
                                       405 {:status 405 :body "method not allowed"}
                                       406 {:status 406 :body "not acceptable"}))}}]
  (cond->
      (ring/ring-handler
       (ring/router router)
       (ring/routes
        (ring/create-default-handler
         {:not-found          (constantly (on-error 404))
          :method-not-allowed (constantly (on-error 405))
          :not-acceptable     (constantly (on-error 406))})))
    (wrap-internal-error {:ui on-internal-error})
    (wrap-resource resource-path)
    wrap-params
    (-wrap-session {:cookie-secret cookie-secret})))

(defn create-system [{:keys [handler port] :or {port 8080}}]
  {:app handler
   :port port})

(defn start [{:keys [app port] :as system}]
  (let [server (run-jetty (fn [req] (app req)) {:port port :join? false})
        local-port (-> server
                       .getConnectors
                       first
                       ^ServerConnector
                       .getLocalPort)]
    (prn :local-port local-port)
    (assoc system
           :server server)))

(defn stop [{:keys [server] :as system}]
  (.stop server)
  (dissoc system :server))
