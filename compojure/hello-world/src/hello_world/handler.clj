(ns hello-world.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn wrap-current-user-id
  "https://github.com/weavejester/compojure/wiki/Middleware"
  [handler]
  (fn [request]
    (let [user-id (-> request :session :user-id)]
      (handler (assoc request :user-id user-id)))))

(defn form
  ""
  [action body]
  (format "<form action=\"%s\" method=\"POST\"><input type=\"hidden\" name=\"__anti-forgery-token\" value=\"%s\" />%s</form>" action *anti-forgery-token* body))


(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/form" []
       (form "/login"
             "<input type=\"text\" name=\"user-id\" placeholder=\"user-id\" /><button>Log In</button>"))
  (GET "/session" request
       (let [{:keys [session]} request]
         (format "session: %s" (pr-str session))))
  (POST "/login" request
        (let [{:keys [session] {:keys [user-id]} :params} request]
          {:body (str "Logged in " user-id)
           :headers {"Content-Type" "text/html"}
           :session (assoc session :user-id user-id)}))
  (POST "/logout" request
        (let [{:keys [session]} request]
          {:body "Logged out"
           :headers {"Content-Type" "text/html"}
           :session (dissoc session :user-id)}))
  (wrap-current-user-id
   (GET "/current-user" {:keys [user-id]}
        (str "The current user ID is: " user-id
             (form "/logout" "<button>Logout</button>"))))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
