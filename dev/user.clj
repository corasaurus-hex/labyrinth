(require '[corasaurus-hex.labyrinth.grid :as g]
         '[corasaurus-hex.labyrinth.grid.export :as e]
         '[corasaurus-hex.labyrinth.grid.export.ir :as ir]
         '[corasaurus-hex.labyrinth.grid.binary-tree :as bt]
         '[corasaurus-hex.labyrinth.grid.specs]
         '[clojure.spec.alpha :as s]
         '[meander.match.delta :as m])

(comment
  (-> (g/->maze 10 10)
      (bt/gen)
      (ir/->ir)
      (e/ir->txt)
      (println))

  {:paths
   [{:cursor [2 2]
     :path [:entrance :north :north :north :east :south :dead-end]}
    {:cursor [2 4]
     :path [:entrance :north :north :north :east :north]}]})
