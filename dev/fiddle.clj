(ns fiddle)

#_
(require '[labyrinth.grid :as g]
         '[labyrinth.grid.export :as e]
         '[labyrinth.grid.export.ir :as ir]
         '[labyrinth.grid.binary-tree :as bt]
         '[labyrinth.grid.specs]
         '[clojure.spec.alpha :as s]
         '[meander.match.delta :as m])

#_
(-> (g/->maze 10 10)
    (bt/gen)
    (ir/->ir)
    (e/ir->txt)
    (println))

#_Agassiz


#_{:paths
   [{:cursor [2 2]
     :path [:entrance :north :north :north :east :south :dead-end]}
    {:cursor [2 4]
     :path [:entrance :north :north :north :east :north]}]}
