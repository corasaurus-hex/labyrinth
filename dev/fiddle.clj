(ns fiddle)

#_
(require '[labyrinth.grid :as g]
         '[labyrinth.grid.export :as e]
         '[labyrinth.grid.export.ir :as ir]
         '[labyrinth.grid.binary-tree :as bt]
         '[labyrinth.grid.specs]
         '[clojure.spec.alpha :as s])

#_
(-> (g/->maze 10 10)
    (bt/gen)
    (ir/->ir)
    (e/ir->txt)
    (println))
