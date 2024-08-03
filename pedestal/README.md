# Hello World #

<http://pedestal.io/pedestal/0.7/guides/hello-world.html>

``` shell
$ mkdir hello-world
$ cd hello-world
$ mkdir src
```

src/hello.clj

``` clojure
(ns hello
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]))
```
