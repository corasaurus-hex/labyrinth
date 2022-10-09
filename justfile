set positional-arguments

test:
    clojure -T:build test

ci:
    clojure -T:build ci

repl:
    clj -M:repl "$@"
