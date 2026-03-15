(ns web.hello.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.session.cookie :as cookie]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :as res]
            [web.hello.util :as util]
            [clojure.edn :as edn]
            [clojure.pprint :refer [pprint]]
            [hiccup2.core :as h]
            [reitit.ring :as ring]))

(defn gen-cookie-secret []
  "https://ring-clojure.github.io/ring/ring.middleware.session.cookie.html
  > :key - The secret key to encrypt the session cookie. Must be a byte array of
  >     exactly 16 bytes."
  (let [buffer (byte-array 16)]
    (.nextBytes (java.security.SecureRandom/getInstanceStrong) buffer)
    buffer))

(defn write-config []
  (let [new-config {:cookie-secret (util/base64-encode (gen-cookie-secret))}]
    (spit "config.edn" (pr-str new-config))
    new-config))

(defn load-config []
  (edn/read-string (slurp "config.edn")))

(def config (if-not (.exists (java.io.File. "config.edn"))
              (write-config)
              (load-config)))

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

(defn hello [_]
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body (html {:title "Hello World!"} [:div
                   [:h1 "Hello"]
                   [:ul
                    [:li [:a {:href "/say"} "say"]]
                    [:li [:a {:href "/info"} "info"]]
                    [:li [:a {:href "/wrong"} "wrong(internal error)"]]
                    [:li [:a {:href "/x"} "not found"]]]])})

(defn info [req]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (with-out-str (pprint req))})

(defn say [{:keys [session]}]
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body (html {}
               [:div
                (when-some [greeting (:greeting session)]
                  [:p "Greeting: " greeting])
                [:form {:action "/say" :method "post"}
                 [:input {:name "greeting" :placeholder "Say something..."}]
                 [:input {:type "submit" :value "Say"}]]])})

(defn say-hello-handler [{:keys [request-method params session]}]
  (when-not (= request-method :post)
    (throw (Exception. "Not allowed method")))
  (when-not (get params "greeting")
    (throw (Exception. "greeting parameter is needed.")))
  (-> (res/redirect "/say" 303)
      (assoc :session (assoc session :greeting (get params "greeting")))))

(defn wrong [req]
  (throw (Exception. "WRONG!!")))

(defn wrap-server [handler]
  (fn [request]
    (assoc-in (handler request) [:headers "Server"] "My Web Server(1.0)")))

(defn wrap-internal-error [handler]
  (fn [req]
    (try 
      (handler req)
      (catch Throwable t
        {:status 500
         :headers {"Content-Type" "text/html; charset=utf-8"}
         :body (html {}
                     [:h1 "Internal Error"]
                     [:p [:span "message: "] [:code (.getMessage t)]])}))))

(defonce server (atom nil))

(defn -wrap-session [handler]
  (let [cookie-secret (-> config
                          :cookie-secret
                          util/base64-decode)]
    (wrap-session handler {:cookie-attrs {:max-age (* 60 60 24 60)
                                          :same-site :lax
                                          :http-only true}
                           :store (cookie/cookie-store {:key cookie-secret})})))
(def router
  (ring/router
   [["/"      {:get hello}]
    ["/info"  {:get info}]
    ["/say"   {:get say
               :post say-hello-handler}]
    ["/wrong" {:get wrong}]]))

(def app
  (-> (ring/ring-handler
       router
       (ring/routes
        ;; (ring/create-resource-handler {:path "/"})
        (ring/create-default-handler
         {:not-found          (constantly {:status 404 :body "not found"})
          :method-not-allowed (constantly {:status 405 :body "method not allowed"})
          :not-acceptable     (constantly {:status 406 :body "not acceptable"})})))
      wrap-internal-error
      (wrap-resource "public")
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
