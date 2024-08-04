# Hello World #

<http://pedestal.io/pedestal/0.7/guides/hello-world.html>

NOTE) Java 11 or higher required

``` shell
> java -version
openjdk version "11.0.19" 2023-04-18 LTS
OpenJDK Runtime Environment Corretto-11.0.19.7.1 (build 11.0.19+7-LTS)
OpenJDK 64-Bit Server VM Corretto-11.0.19.7.1 (build 11.0.19+7-LTS, mixed mode)
```

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
