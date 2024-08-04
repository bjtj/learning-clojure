CLJ ?= clj

all: run

run:
	$(CLJ) -M --main cljs.main --compile hello-world.core --repl

run-nobrowser:
	$(CLJ) -M --main cljs.main --repl-opts "{:launch-browser false}" --compile hello-world.core --repl

help:
	$(CLJ) -M -m cljs.main --help

build:
	$(CLJ) -M -m cljs.main --optimizations advanced -c hello-world.core

serve:
	$(CLJ) -M -m cljs.main --serve

build-nodejs:
	$(CLJ) -M -m cljs.main --target node --output-to main.js -c hello-world.core

node-repl:
	$(CLJ) -M -m cljs.main --repl-env node

build-run:
	$(CLJ) -M -m cljs.main -c hello-world.core -r

.PHONY: all run run-nobrowser
