(ns labyrinth.grid
  (:require [clojure.spec.alpha :as s]
            [labyrinth.grid.specs]))

(def opposite-directions
  {:north :south
   :south :north
   :east  :west
   :west  :east})

(defn ->coords
  "Generates a list of vectors of all coordinates for a given width and height maze"
  [width height]
  (for [col (range 1 (inc width))
        row (range 1 (inc height))]
    [col row]))

(defn ->cell
  "Generates a grid cell where each value is a wall."
  []
  {:north :wall
   :south :wall
   :east :wall
   :west :wall})

(defn ->cells
  "Generates a map of cells that has keys of coordinates and values of edges that are all walls."
  [width height]
  (zipmap (->coords width height)
          (repeat (->cell))))

(defn ->maze
  "Generates a maze of a specific width and height."
  [width height]
  {:width width
   :height height
   :cursor [1 1]
   :cells (->cells width height)})

(defn wall-at?
  "Returns true if there is a wall in a direction for a cell, otherwise false."
  [cell direction]
  (if cell
    (= (cell direction) :wall)
    false))

(defn wall-between?
  "Checks to see if there is a wall between two cells. Cells may be nil."
  [[cell direction]
   [other-cell other-direction]]
  (or (wall-at? cell direction)
      (wall-at? other-cell other-direction)))

(defn coord-in-direction
  "Gets you the coordinate for a given direction given the current cursor position. Returns nil if there is none."
  [{:keys [width height]} [col row] direction]
  (case direction
    :north (when (< row height) [col (inc row)])
    :east  (when (< col width)  [(inc col) row])
    :south (when (< 1 row)      [col (dec row)])
    :west  (when (< 1 col)      [(dec col) row])))

(defn move-cursor
  "Changes the cursor to the passed coordinate."
  [maze coord]
  (assoc maze :cursor coord))

(defn maze->perimeter
  "Calculate the perimeter of a maze."
  [{:keys [width height]}]
  (+ (* 2 width)
     (* 2 height)))

(defn maze->facets
  "Gets all the facets on the perimeter of a maze, in order by walking the
  perimeter starting with the southwest corner. Used for walking the permiter
  to find entrances and exits.
  NOTE: Corner coordinates are repeated, but with different directions."
  [{:keys [width height]}]
  (concat (for [row (range 1 (inc height))] [[1 row] :west])      ;; west edge coordinates, from south to north
          (for [col (range 1 (inc width))] [[col height] :north]) ;; north edge coordinates, from west to east
          (for [row (range height 0 -1)] [[width row] :east])     ;; east edge coordinates, from north to south
          (for [col (range width 0 -1)] [[col 1] :south])))       ;; south edge coordinates, from east to west

(defn perimeter-walk->facet
  "Walks a given number of edge cell around a maze perimiter and gives the coordinates of the cell it stops on."
  [maze steps]
  (-> (maze->facets maze)
      (cycle)
      (nth (dec steps))))

(defn change-edge-type
  "Changes the edge type for a cell."
  [maze {:keys [coord direction edge-type]}]
  (update-in maze [:cells coord] #(assoc %1 direction edge-type)))

(s/fdef change-edge-type
  :args (s/cat :grid/maze (s/keys :req-un [:grid/coord :cell/direction :cell/edge-type]))
  :ret :grid/maze)

(defn add-door
  "Adds an door at the coordinate in the direction specified."
  [maze options]
  (change-edge-type maze (assoc options :edge-type :door)))

(s/fdef add-door
  :args (s/cat :grid/maze (s/keys :req-un [:grid/coord :cell/direction]))
  :ret :grid/maze)

(defn walk-and-add-outlet
  [maze {:keys [steps outlet-type]}]
  (let [[coord direction] (perimeter-walk->facet maze steps)]
    (change-edge-type maze {:coord coord, :direction direction, :edge-type outlet-type})))

(defn walk-and-add-exit
  [maze steps]
  (walk-and-add-outlet maze {:steps steps, :outlet-type :exit}))

(defn walk-and-add-entrance
  [maze steps]
  (walk-and-add-outlet maze {:steps steps, :outlet-type :entrance}))

(defn link-cell
  "Two-way links one cell to another in the specified direction."
  [maze coord direction]
  (let [other-coord (coord-in-direction maze coord direction)
        other-direction (opposite-directions direction)]
    (-> maze
        (add-door {:coord coord, :direction direction})
        (add-door {:coord other-coord, :direction other-direction}))))
