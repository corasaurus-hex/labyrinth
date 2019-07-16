(ns labyrinth.grid.binary-tree
  (:require [labyrinth.grid :as g]))

(defn random-direction
  "Returns a random direction, either :north or :east."
  []
  (rand-nth [:north :east]))

(defn end-coord?
  [{:keys [width height]} [col row]]
  (and (= col width) (= row height)))

(defn at-end?
  "Are we at the end of the maze, meaning the walk function should stop."
  [{:keys [cursor] :as maze}]
  (end-coord? maze cursor))

(defn next-cursor-pos
  "Gets the next position the walk function should move the cursor to. Returns nil if at the end of the maze."
  [{:keys [width] [col row] :cursor :as maze}]
  (when-not (at-end? maze)
    (if (= col width)
      [1 (inc row)]      ;; at the right edge of the grid, move all the way left and up a row
      [(inc col) row]))) ;; not at the right edge yet, keep row the same and move right

(defn penultimate?
  "Are we at the penultimate cell of the maze, meaning at-end? returns true for the next cell."
  [maze]
  (end-coord? maze (next-cursor-pos maze)))

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

(defn next-steps
  "Returns the next list of of operations to perform on the maze"
  [{cursor :cursor :as maze}]
  (when-not (at-end? maze)
    (cond-> [[:link [cursor (next-direction-to-link maze)]]
             [:move (next-cursor-pos maze)]]
      (penultimate? maze) (conj [:add-outlets]))))

(defn add-outlets
  "Adds an exit and an entrance to the maze."
  [maze]
  (let [perimeter (g/maze->perimeter maze)
        quarter-perimeter (/ perimeter 4)
        three-quarter-perimeter (* 3 quarter-perimeter)
        entry-steps (inc (rand-int quarter-perimeter))
        exit-steps (+ three-quarter-perimeter entry-steps)]
    (-> maze
        (g/walk-and-add-entrance entry-steps)
        (g/walk-and-add-exit exit-steps))))

(defn do-step
  "Perform an operation on the maze, returning the changed maze. If op is not recognized then just return the maze."
  [maze [op payload]]
  (case op
    :link (apply g/link-cell maze payload)
    :move (g/move-cursor maze payload)
    :add-outlets (add-outlets maze)
    maze))

(defn gen
  "Given a grid maze, generate paths through the maze using the binary-tree algorithm."
  [{:keys [width height] :as maze}]
  (cond
    (= 1 width height) (do-step maze [:add-outlets])
    (at-end? maze) maze
    :else (recur
           (reduce do-step
                   maze
                   (next-steps maze)))))
