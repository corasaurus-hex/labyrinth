(ns labyrinth.grid.specs
  (:require [clojure.spec.alpha :as s]))

(s/def :grid/width pos-int?)
(s/def :grid/height pos-int?)
(s/def :grid/col pos-int?)
(s/def :grid/row pos-int?)
(s/def :grid/coordinate (s/tuple :grid/col :grid/row))
(s/def :grid/cursor :grid/coordinate)
(s/def :cell/edge-type #{:door :wall :exit :entrance})
(s/def :cell/edge :cell/edge-type)
(s/def :cell-edge/north :cell/edge)
(s/def :cell-edge/south :cell/edge)
(s/def :cell-edge/east :cell/edge)
(s/def :cell-edge/west :cell/edge)
(s/def :grid/cell (s/keys :req-un [:cell-edge/north :cell-edge/south :cell-edge/east :cell-edge/west]))
(s/def :grid/cells (s/map-of :grid/coordinate :grid/cell))
(s/def :grid/maze (s/keys :req-un [:grid/width :grid/height :grid/cells :grid/cursor]))

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
