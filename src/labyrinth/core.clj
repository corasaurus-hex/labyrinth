(ns labyrinth.core
  (:require [labyrinth.grid :as grid]
            [labyrinth.grid.binary-tree :as binary-tree]))

(grid/->maze 5 5)
(binary-tree/random-direction)
