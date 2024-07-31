# Learning Clojure #

<https://clojure.org/>

## Neil ##

<https://github.com/babashka/neil>

e.g.) new project

``` shell
$ neil new app hello-world
```

## babashka ##

<https://babashka.org/>

### Differences with Clojure ###

<https://book.babashka.org/#differences-with-clojure>

> Babashka is implemented using the [Small Clojure Interpreter](https://github.com/borkdude/sci). This means that a snippet or script is not compiled to JVM bytecode, but executed form by form by a runtime which implements a substantial subset of Clojure. Babashka is compiled to a native binary using [GraalVM](https://github.com/oracle/graal). It comes with a selection of built-in namespaces and functions from Clojure and other useful libraries. The data types (numbers, strings, persistent collections) are the same. Multi-threading is supported (`pmap`, `future`).
> 
> Differences with Clojure:
> 
> -   A pre-selected set of Java classes are supported. You cannot add Java classes at runtime.
> -   Interpretation comes with overhead. Therefore loops are slower than in Clojure on the JVM. In general interpretation yields slower programs than compiled programs.
> -   No `deftype`, `definterface` and unboxed math.
> -   `defprotocol` and `defrecord` are implemented using multimethods and regular maps. Ostensibly they work the same, but under the hood there are no Java classes that correspond to them.
> -   Currently `reify` works only for one class at a time
> -   The `clojure.core.async/go` macro is not (yet) supported. For compatibility it currently maps to `clojure.core.async/thread`. More info [here](https://book.babashka.org/#core_async).

## GraalVM ##

<https://www.graalvm.org/>

> GraalVM compiles your Java applications ahead of time into standalone binaries that start instantly, provide peak performance with no warmup, and use fewer resources.




## The (Clojure) "JVM Slow Startup Time" Myth ##

<https://blog.ndk.io/jvm-slow-startup.html>

> Conclusion: JVM startup time is not a problem. **Clojure startup time** is. But why? That’s for another post.

## Why is Clojure bootstrapping so slow? ##

<https://blog.ndk.io/clojure-bootstrapping.html>

> So the short answer is this: Clojure programs start slowly because every Clojure program loads the main Clojure namespace `clojure.core` before executing. This takes time.
