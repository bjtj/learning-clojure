CLJ ?= clj

all: run

run:
	$(CLJ) -M -m figwheel.main

repl:
	$(CLJ) -M -m figwheel.main --compile hello.cruel-world --repl

cruel:
	$(CLJ) -M -m figwheel.main --build cruel --repl

optimize:
	$(CLJ) -M -m figwheel.main --optimizations whitespace --build-once cruel

.PHONY: all run
