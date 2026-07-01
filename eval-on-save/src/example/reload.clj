(ns example.reload
  (:require clojure.repl
            [clojure.string :as str]
            [clojure.tools.namespace.dir :as dir]
            [clojure.tools.namespace.reload :as reload]
            [clojure.tools.namespace.track :as track]
            [clojure.tools.namespace.repl :as repl]))

(defonce global-tracker (atom (track/tracker)))

(defn print-and-return [tracker]
  (if-let [e (::reload/error tracker)]
    (do (when (thread-bound? #'*e)
          (set! *e e))
        (prn :error-while-loading (::reload/error-ns tracker))
        (clojure.repl/pst e)
        e)
    :ok))

(defn refresh [tracker dirs]
  (let [new-tracker (dir/scan-dirs tracker dirs)
        _ (print-and-return new-tracker)
        new-tracker (reload/track-reload (assoc new-tracker ::track/unload []))]
    new-tracker))

