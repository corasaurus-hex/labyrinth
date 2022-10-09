(ns corasaurus-hex.labyrinth.grid.export.ir
  (:require [corasaurus-hex.labyrinth.grid :as g]))

(defn surrounding-cell-coords->surrounding-cells
  [cells cell-coords]
  (reduce-kv
   #(assoc %1 %2 (cells %3))
   {}
   cell-coords))

(defn horizontal-coord->surrounding-cell-coords
  [[col row]]
  (let [grid-col (/ col 2)]
    {:north [grid-col (-> row (+ 1) (/ 2))]
     :south [grid-col (-> row (- 1) (/ 2))]}))

(defn horizontal-coord->surrounding-cells
  [cells coord]
  (surrounding-cell-coords->surrounding-cells
   cells
   (horizontal-coord->surrounding-cell-coords coord)))

(defn horizontal-coord->block
  "Given maze grid cells and an IR intersection coordinate, this determines what kind of wall type should be in this position in the IR."
  [cells coord]
  (let [{:keys [north south]} (horizontal-coord->surrounding-cells cells coord)]
    (cond
      (g/wall-between? [north :south] [south :north]) :horizontal
      (and (nil? north) (= :entrance (:north south))) :north-entrance
      (and (nil? north) (= :exit (:north south))) :north-exit
      (and (nil? south) (= :entrance (:south north))) :south-entrance
      (and (nil? south) (= :exit (:south north))) :south-exit
      :else :horizontal-empty)))

(defn vertical-coord->surrounding-cell-coords
  [[col row]]
  (let [grid-row (/ row 2)]
    {:east [(-> col (+ 1) (/ 2)) grid-row]
     :west [(-> col (- 1) (/ 2)) grid-row]}))

(defn vertical-coord->surrounding-cells
  [cells coord]
  (surrounding-cell-coords->surrounding-cells
   cells
   (vertical-coord->surrounding-cell-coords coord)))

(defn vertical-coord->block
  "Given maze grid cells and an IR intersection coordinate, this determines what kind of wall type should be in this position in the IR."
  [cells coord]
  (let [{:keys [east west]} (vertical-coord->surrounding-cells cells coord)]
    (cond
      (g/wall-between? [east :west] [west :east]) :vertical
      (and (nil? west) (= :entrance (:west east))) :west-entrance
      (and (nil? west) (= :exit (:west east))) :west-exit
      (and (nil? east) (= :entrance (:east west))) :east-entrance
      (and (nil? east) (= :exit (:east west))) :east-exit
      :else :vertical-empty)))

(def intersection-wall-directions->block
  {#{:north :south} :vertical
   #{:east :west} :intersection-horizontal
   #{:south :east} :down-and-right
   #{:south :west} :down-and-left
   #{:north :east} :up-and-right
   #{:north :west} :up-and-left
   #{:north :south :east} :vertical-and-right
   #{:north :south :west} :vertical-and-left
   #{:south :east :west} :down-and-horizontal
   #{:north :east :west} :up-and-horizontal
   #{:north :south :east :west} :vertical-and-horizontal
   #{:west} :half-left
   #{:north} :half-up
   #{:east} :half-right
   #{:south} :half-down})

(defn intersection-coord->surrounding-cell-coords
  "Finds the coordinates of maze grid cells surrounding an intersection coordinate."
  [[col row]]
  (letfn [(scale-down [n] (-> n (- 1) (/ 2)))
          (scale-down-and-one [n] (-> n (scale-down) (+ 1)))]
    {:south-west [(scale-down col) (scale-down row)]
     :north-west [(scale-down col) (scale-down-and-one row)]
     :north-east [(scale-down-and-one col) (scale-down-and-one row)]
     :south-east [(scale-down-and-one col) (scale-down row)]}))

(defn intersection-coord->surrounding-cells
  "Finds the grid cells surrounding an intersection coordinate."
  [cells coord]
  (surrounding-cell-coords->surrounding-cells
   cells
   (intersection-coord->surrounding-cell-coords coord)))

(defn intersection-coord->wall-directions
  "Given an intersection coordinate, return a set of the directions there are walls."
  [cells coord]
  (let [surrounding-cells (intersection-coord->surrounding-cells cells coord)
        {:keys [north-west north-east south-east south-west]} surrounding-cells]
    (cond-> #{}
      (g/wall-between? [north-west :east]
                       [north-east :west]) (conj :north)
      (g/wall-between? [north-east :south]
                       [south-east :north]) (conj :east)
      (g/wall-between? [south-east :west]
                       [south-west :east]) (conj :south)
      (g/wall-between? [south-west :north]
                       [north-west :south]) (conj :west))))

(defn intersection-coord->block
  "Given maze grid cells and an IR intersection coordinate, this determines what kind of wall type should be in this position in the IR."
  [cells coord]
  (-> cells
      (intersection-coord->wall-directions coord)
      (intersection-wall-directions->block)))

(defn ir-coord->block
  "Given maze grid cells and an IR coordinate, this determine what kind of wall type should be in this position in the IR."
  [cells [col row :as coord]]
  (cond
    (and (odd? col) (odd? row)) (intersection-coord->block cells coord)
    (and (odd? col) (even? row)) (vertical-coord->block cells coord)
    (and (even? col) (odd? row)) (horizontal-coord->block cells coord)
    :else :empty))

(defn ->ir
  "Generates a hashmap that is the intermediate representation between a maze and any export type."
  [{:keys [width height cells] :as _maze}]
  (let [ir-width (inc (* 2 width))
        ir-height (inc (* 2 height))]
    {:width ir-width
     :height ir-height
     :blocks (into {}
                   (map #(vector % (ir-coord->block cells %)))
                   (g/->coords ir-width ir-height))}))
