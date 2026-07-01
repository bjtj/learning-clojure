# Learning Clojure

## copy `deps.template.edn` to new project

1. Create a new project directory:

``` shell
mkdir <project>
```

2. Copy the template file into the project directory and rename it:

``` shell
cp deps.template.edn <project>/deps.edn
```

## dev (nrepl.cmdline with cider middleware)

``` shell
clojure -M:dev
```

## run main (alias)

e.g.) deps.edn

``` clojure
{:paths ["src" "resources"]
 :deps {org.clojure/clojure     {:mvn/version "1.12.0"}
        ...
        }
 :aliases
 {:dev {:extra-deps {nrepl/nrepl       {:mvn/version "1.3.0"}
                     cider/cider-nrepl {:mvn/version "0.58.0"}}
        :main-opts ["-m" "web.hello.core"]}}}
```

e.g.) `nrepl.cmdline/-main`

``` clojure
(defn run-nrepl []
  (try
    (when-let [nrepl-main (requiring-resolve 'nrepl.cmdline/-main)]
      (nrepl-main "--middleware" "[cider.nrepl/cider-middleware]"))
    (catch Exception e
      (prn :error (.getMessage e)))))
```
