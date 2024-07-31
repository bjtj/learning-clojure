# mount #

<https://github.com/tolitius/mount>

``` clojure
(require '[mount.core :refer [defstate]])
```

## Creating State ##

``` clojure
(defstate conn :start (create-conn)
               :stop (disconnect conn))
```

## Using State ##

``` clojure
(ns app
  (:require [above :refer [conn]]))
```

## Documentation String ##

``` clojure
(defstate answer
  "answer to the ultimate question of life universe and everything"
  :start (+ 1 41))
```

``` clojure
(doc answer)
-------------------------
dev/answer
  answer to the ultimate question of life universe and everything
```


## The Importance of Being Reloadable ##

e.g.)

``` clojure
(defn go []
  (start)
  :ready)

(defn reset []
  (stop)
  (tn/refresh :after 'dev/go))
```

## Start and Stop Order ##

``` clojure
dev=> (reset)
08:21:39.430 [nREPL-worker-1] DEBUG mount - << stopping..  nrepl
08:21:39.431 [nREPL-worker-1] DEBUG mount - << stopping..  conn
08:21:39.432 [nREPL-worker-1] DEBUG mount - << stopping..  config

:reloading (app.config app.nyse app.utils.datomic app)

08:21:39.462 [nREPL-worker-1] DEBUG mount - >> starting..  config
08:21:39.463 [nREPL-worker-1] DEBUG mount - >> starting..  conn
08:21:39.481 [nREPL-worker-1] DEBUG mount - >> starting..  nrepl
:ready
```

## Start and Stop Parts of Application ##

``` clojure
(mount/start #'app.config/config #'app.nyse/conn)
...
(mount/stop #'app.config/config #'app.nyse/conn)
```

> which will _only_ start/stop `config` and `conn` (won't start/stop any other states).


## Start an Application Without Certain States ##

``` clojure
(mount/start-without #'app.feeds/feed-listener
                     #'app/nrepl)
```

> which will start an application without starting `feed-listener` and `nrepl` states.


## Swapping Alternate Implementations ##

### Swapping States with Values ###

### Swapping States with States ###

``` clojure
(mount/start-with-states {#'app.neo/db        {:start #(connect test-config)
                                               :stop #(disconnect db)}
                          #'app.neo/publisher {:start #(create-pub test-config)
                                               :stop #(close-pub publisher)}})
```

> One thing to note is whenever

``` clojure
(mount/stop)
```

> is run after `start-with`/`start-with-states`, it rolls back to an original "state of states", i.e. `#'app.neo/db` is `#'app.neo/db` again. So subsequent calls to `(mount/start)` or even to `(mount/start-with {something else})` will start from a clean slate.


test/core/mount/test/start_with_states.cljc

<https://github.com/tolitius/mount/blob/master/test/core/mount/test/start_with_states.cljc>


# Stop an Application Except Certain States #

## Recompiling Namespaces with Running States ##

### :on-reload ###

``` clojure
(defstate ^{:on-reload :noop}
          mem-db :start (connect config)
                 :stop (disconnect mem-db))
```

``` clojure
(defstate ^{:on-reload :stop}
          mem-db :start (connect config)
                 :stop (disconnect mem-db))
```


## Cleaning up Deleted States ##

``` clojure
dev=> (defstate won't-be-here-long :start (println "I am starting... ")
                                   :stop (println "I am stopping... "))
#'dev/won't-be-here-long
dev=>

dev=> (mount/start #'dev/won't-be-here-long)
INFO  app.utils.logging - >> starting..  #'dev/won't-be-here-long
I am starting...
{:started ["#'dev/won't-be-here-long"]}
dev=>
```

> "deleting" it from REPL, and starting all the states:

``` clojure
dev=> (ns-unmap 'dev 'won't-be-here-long)
nil
dev=> (mount/start)

"<< stopping.. #'dev/won't-be-here-long (it was deleted)"
I am stopping...

INFO  app.utils.logging - >> starting..  #'app.conf/config
INFO  app.utils.logging - >> starting..  #'app.db/conn
INFO  app.utils.logging - >> starting..  #'app.www/nyse-app
INFO  app.utils.logging - >> starting..  #'app.example/nrepl
{:started ["#'app.conf/config" "#'app.db/conn" "#'app.www/nyse-app" "#'app.example/nrepl"]}
```
