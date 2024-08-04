@echo off
setlocal

rem https://github.com/clojure/clojurescript/releases/tag/r1.11.132
IF NOT EXIST cljs.jar (
   curl -OL https://github.com/clojure/clojurescript/releases/download/r1.11.132/cljs.jar
)

set "opt=%1"

if /i "%opt%"=="" goto run
if /i "%opt%"=="run" goto run
if /i "%opt%"=="run-nobrowser" goto run-nobrowser
if /i "%opt%"=="help" goto help
if /i "%opt%"=="build" goto build
if /i "%opt%"=="serve" goto serve
if /i "%opt%"=="build-nodejs" goto build-nodejs
if /i "%opt%"=="node-repl" goto node-repl
if /i "%opt%"=="build-run" goto build-run
goto invalid

:run
  java -cp "cljs.jar;src" cljs.main --compile hello-world.core --repl
  goto end

:run-nobrowser
  java -cp "cljs.jar;src" cljs.main --repl-opts "{:launch-browser false}" --compile hello-world.core --repl
  goto end

:help
  java -cp "cljs.jar;src" cljs.main --help
  goto end

:build
  java -cp "cljs.jar;src" cljs.main --optimizations advanced -c hello-world.core
  goto end

:serve
  java -cp "cljs.jar;src" cljs.main --serve
  goto end

:build-nodejs
  java -cp "cljs.jar;src" cljs.main --target node --output-to main.js -c hello-world.core
  goto end

:node-repl
  java -cp "cljs.jar;src" cljs.main --repl-env node
  goto end

:build-run
  java -cp "cljs.jar;src" cljs.main -c hello-world.core -r
  goto end

:invalid
  echo Invalid option: %opt%
  goto end

:end
  endlocal
