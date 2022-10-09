(ns corasaurus-hex.labyrinth.grid.specs
  (:require [clojure.spec.alpha :as s]))

(defn all-coords?
  [{:keys [width height cells] :as _maze}]
  (= (sort (keys cells))
     (for [c (range 1 (inc width))
           r (range 1 (inc height))]
       [c r])))

(s/def :grid/width pos-int?)
(s/def :grid/height pos-int?)
(s/def :grid/col pos-int?)
(s/def :grid/row pos-int?)
(s/def :grid/coordinate (s/tuple :grid/col :grid/row))
(s/def :grid/coord :grid/coordinate)
(s/def :grid/cursor :grid/coordinate)
(s/def :cell/edge-type #{:door :wall :exit :entrance})
(s/def :cell/edge :cell/edge-type)
(s/def :cell/direction #{:north :south :east :west})
(s/def :cell-edge/north :cell/edge)
(s/def :cell-edge/south :cell/edge)
(s/def :cell-edge/east :cell/edge)
(s/def :cell-edge/west :cell/edge)
(s/def :grid/facet (s/tuple :grid/coordinate :cell/direction))
(s/def :grid/cell (s/keys :req-un [:cell-edge/north :cell-edge/south :cell-edge/east :cell-edge/west]))
(s/def :grid/cells (s/map-of :grid/coordinate :grid/cell))
(s/def :grid/maze (s/and (s/keys :req-un [:grid/width :grid/height :grid/cells :grid/cursor])
                         all-coords?))

(s/def :binary-tree/link-from :grid/coordinate)
(s/def :binary-tree/link-direction :cell/direction)
(s/def :binary-tree/link-step (s/tuple #(= :link %) (s/tuple :binary-tree/link-from :binary-tree/link-direction)))
(s/def :binary-tree/move-step (s/tuple #(= :move %) :grid/coordinate))
(s/def :binary-tree/add-outlets-step (s/tuple #(= :add-outlets %)))

(s/def :export-ir/width pos-int?)
(s/def :export-ir/height pos-int?)
(s/def :export-ir/col pos-int?)
(s/def :export-ir/row pos-int?)
(s/def :export-ir/coordinate (s/tuple :export-ir/col :export-ir/row))
(s/def :export-ir/block #{:horizontal
                          :vertical
                          :down-and-right
                          :down-and-left
                          :up-and-right
                          :up-and-left
                          :vertical-and-right
                          :vertical-and-left
                          :down-and-horizontal
                          :up-and-horizontal
                          :vertical-and-horizontal
                          :half-left
                          :half-up
                          :half-right
                          :half-down
                          :empty})
(s/def :export-ir/blocks (s/map-of :export-ir/coordinate :export-ir/block))
(s/def :export-ir/ir (s/keys :req-un [:export-ir/width :export-ir/height :export-ir/blocks]))
