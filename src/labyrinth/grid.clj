(ns labyrinth.grid)

(def opposite-directions
  {:north :south
   :south :north
   :east  :west
   :west  :east})

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


(defn ->coords
  "Generates a list of vectors of all coordinates for a given width and height maze"
  [width height]
  (for [col (range 1 (inc width))
        row (range 1 (inc height))]
    [col row]))

(defn ->edges
  "Generates a grid edge where each value is a wall."
  []
  {:north :wall
   :south :wall
   :east :wall
   :west :wall})

(defn ->cells
  "Generates a map of cells that has keys of coordinates and values of edges that are all walls."
  [width height]
  (zipmap (->coords width height)
          (repeatedly ->edges)))

(defn ->maze
  "Generates a maze of a specific width and height."
  [width height]
  {:width width
   :height height
   :cursor [1 1]
   :cells (->cells width height)})

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
  [maze cell]
  (assoc maze :cursor cell))

(defn change-edge-type
  "Changes the edge type for a cell."
  [maze coord direction edge-type]
  (update-in maze [:cells coord] #(assoc %1 direction edge-type)))

(defn add-door
  "Adds an door at the coordinate in the direction specified."
  [maze coord direction]
  (change-edge-type maze coord direction :door))

(defn add-entrance
  "Adds an entrance at the coordinate in the direction specified."
  [maze coord direction]
  (change-edge-type maze coord direction :entrance))

(defn add-exit
  "Adds an exit at the coordinate in the direction specified."
  [maze coord direction]
  (change-edge-type maze coord direction :exit))

(defn link-cell
  "Two-way links one cell to another in the specified direction."
  [maze cell direction]
  (let [other-cell (coord-in-direction maze cell direction)
        other-direction (opposite-directions direction)]
    (-> maze
        (add-door cell direction)
        (add-door other-cell other-direction))))

(defn maze->perimeter
  "Calculate the perimeter of a maze."
  [{:keys [width height]}]
  (+ (* 2 width)
     (* 2 height)))

(defn maze->permiter-coord+edges
  "Gets all the coords on the perimeter of a maze, in order, including the edge,
  by walking the perimeter starting with the southwest corner. Used for walking
  the permiter to find entrances and exits.
  NOTE: Corner coordinates are repeated, but with different edges."
  [{:keys [width height]}]
  (concat (for [row (range 1 (inc height))] [[1 row] :west])      ;; west edge coordinates, from south to north
          (for [col (range 1 (inc width))] [[col height] :north]) ;; north edge coordinates, from west to east
          (for [row (range height 0 -1)] [[width row] :east])     ;; east edge coordinates, from north to south
          (for [col (range width 0 -1)] [[col 1] :south])))       ;; south edge coordinates, from east to west

(defn perimeter-walk->coord+edge
  "Walks a given number of cell edges around a maze perimiter and gives the coordinates of the cell it stops on."
  [maze steps]
  (-> (maze->permiter-coord+edges maze)
      (cycle)
      (nth (dec steps))))
