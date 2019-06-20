(ns labyrinth.grid
  (:require [clojure.spec.alpha :as s]))

(s/def ::width (s/and int? pos?))
(s/def ::height (s/and int? pos?))
(s/def ::direction #{:north :south :east :west})
(s/def ::exits (s/coll-of ::direction
                          :kind set?
                          :min-count 0
                          :max-count 4))
(s/def ::row (s/and int? pos?))
(s/def ::col (s/and int? pos?))
(s/def ::coordinate (s/tuple ::col ::row))
(s/def ::cursor ::coordinate)
(s/def ::cells (s/map-of
                ::coordinate
                ::exits))
(s/def ::maze (s/keys :req-un
                      [::width ::height ::cells ::cursor]))

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

(defn ->cells
  "Generates a map of cells that has keys of coordinates and values of empty sets eventually to be filled with exit directions."
  [width height]
  (reduce #(assoc %1 %2 #{})
          {}
          (->coords width height)))

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

(defn link-cell
  "Two-way links one cell to another in the specified direction."
  ([maze [cell direction]]
   (link-cell maze cell direction))
  ([maze cell direction]
   (let [other-cell (coord-in-direction maze cell direction)
         other-dir (opposite-directions direction)]
     (-> maze
         (update-in [:cells cell] #(conj %1 direction))
         (update-in [:cells other-cell] #(conj %1 other-dir))))))

(defn add-exits
  [maze]
  maze)
