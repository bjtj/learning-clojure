(ns mdns.core
  (:import (javax.jmdns JmDNS ServiceEvent ServiceListener ServiceInfo)
           (java.net InetAddress)))

(def jmdns (JmDNS/create (InetAddress/getLocalHost)))

(defn reg-mdns-service [service-type host port text]
  (let [service-info (ServiceInfo/create service-type host port text)]
    (. jmdns registerService service-info)))

(defn unreg-all-mdns-service []
  (. jmdns unregisterAllServices))

(defn discover [service-type]
  (let [listener (reify ServiceListener
                   (^void serviceAdded [this ^ServiceEvent event]
                    (prn :service-added (. event getInfo)))
                   (^void serviceRemoved [this ^ServiceEvent event]
                    (prn :service-removed (. event getInfo)))
                   (^void serviceResolved [this ^ServiceEvent event]
                    (prn :service-resolved (. event getInfo))))]
    (prn :discover)
    (. jmdns addServiceListener service-type listener)))


(comment
  (discover "_myapp._tcp.local.")
  (reg-mdns-service "_myapp._tcp.local." "my-clj-mdns-service" 1234 "path=index.html")
  (unreg-all-mdns-service)
  ;; 
  )
