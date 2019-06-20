(ns labyrinth.grid.binary-tree
  (:require [labyrinth.grid :as g]))

(defn random-direction
  "Returns a random direction, either :north or :east."
  []
  ([:north :east] (rand-int 2)))

(defn at-end?
  "Are we at the end of the maze, meaning the walk function should stop."
  [{:keys [width height] [col row] :cursor}]
  (and (= col width) (= row height)))

(defn next-coord-to-link
  "Gets the next coordinate to link the cursor to. Returns nil if at the end of the maze."
  [{:keys [width height] [col row] :cursor :as maze}]
   (when-not (at-end? maze)
     (let [max-col (= col width)
           max-row (= row height)]
       (cond
         max-row (g/coord-in-direction maze :east)
         max-col (g/coord-in-direction maze :north)
         :else   (g/coord-in-direction maze (random-direction))))))

(defn next-cursor-pos
  "Gets the next position the walk function should move the cursor to. Returns nil if at the end of the maze."
  [{:keys [width] [col row] :cursor :as maze}]
  (when-not (at-end? maze)
    (if (= col width)
      [1 (inc row)]      ;; at the right edge of the grid, move all the way left and up a row
      [(inc col) row]))) ;; not at the right edge yet, keep row the same and move right

(defn step
  "Returns the next list of of operations to perform on the maze"
  [{cursor :cursor :as maze}]
   (if (at-end? maze)
     [[:add-exits]]
     [[:link [cursor (next-coord-to-link maze)]]
      [:move (next-cursor-pos maze)]]))
