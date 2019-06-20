(ns labyrinth.grid.binary-tree
  (:require [labyrinth.grid :as g]))

(defn random-direction
  "Returns a random direction, either :north or :east."
  []
  (rand-nth [:north :east]))

(defn at-end?
  "Are we at the end of the maze, meaning the walk function should stop."
  [{:keys [width height] [col row] :cursor}]
  (and (= col width) (= row height)))

(defn penultimate?
  "Are we at the penultimate cell of the maze, meaning at-end? returns true for the next cell."
  [{:keys [width height] [col row] :cursor}]
  (and (= (inc col) width) (= row height)))

(defn next-direction-to-link
  "Gets the next coordinate to link the cursor to. Returns nil if at the end of the maze."
  [{:keys [width height] [col row] :cursor :as maze}]
  (when-not (at-end? maze)
    (let [max-col (= col width)
          max-row (= row height)]
      (cond
        max-row :east
        max-col :north
        :else (random-direction)))))

(defn next-cursor-pos
  "Gets the next position the walk function should move the cursor to. Returns nil if at the end of the maze."
  [{:keys [width] [col row] :cursor :as maze}]
  (when-not (at-end? maze)
    (if (= col width)
      [1 (inc row)]      ;; at the right edge of the grid, move all the way left and up a row
      [(inc col) row]))) ;; not at the right edge yet, keep row the same and move right

(defn next-steps
  "Returns the next list of of operations to perform on the maze"
  [{cursor :cursor :as maze}]
  (cond-> [[:link [cursor (next-direction-to-link maze)]]
           [:move (next-cursor-pos maze)]]
    (penultimate? maze) (conj [[:add-exits]])))

(defn do-step
  "Perform an operation on the maze, returning the changed maze. If op is not recognized then just return the maze."
  [maze [op payload]]
  (case op
    :link (g/link-cell maze payload)
    :move (g/move-cursor maze payload)
    :add-exits (g/add-exits maze)
    maze))

(defn gen
  "Given a grid maze, generate paths through the maze using the binary-tree algorithm."
  [maze]
  (if (at-end? maze)
    maze
    (recur
     (reduce do-step
             maze
             (next-steps maze)))))
