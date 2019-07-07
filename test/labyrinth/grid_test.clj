(ns labyrinth.grid-test
  (:require [labyrinth.grid :as g]
            [clojure.spec.alpha :as s]
            [labyrinth.grid.specs]
            [clojure.test :refer [deftest testing is are]]))

(deftest ->coords
  (testing "generates a list of coordinates"
    (is (= [[1 1] [1 2] [1 3] [2 1] [2 2] [2 3]] (g/->coords 2 3)))))

(deftest ->cell
  (testing "generates a walled-off cell"
    (is (= {:north :wall, :south :wall, :east :wall, :west :wall}
           (g/->cell)))))

(deftest ->maze
  (testing "creates a valid maze"
    (let [msg (s/explain-str :grid/maze (g/->maze 2 3))]
      (is (= "Success!\n" msg) msg))))

(deftest wall-at?
  (testing "returns false when no cell is passed in"
    (is (not (g/wall-at? nil :north))))
  (testing "returns true when there is a wall"
    (is (g/wall-at? {:north :wall} :north)))
  (testing "returns false when there is no wall"
    (is (not (g/wall-at? {:north :door} :north)))))

(deftest wall-between?
  (testing "returns false when no cells are passed"
    (is (not (g/wall-between? [nil :south] [nil :north]))))
  (testing "returns true when there is a wall between"
    (is (g/wall-between? [{:south :wall} :south] [{:north :wall} :north])))
  (testing "returns true when a cell is missing but there is a wall"
    (is (g/wall-between? [{:south :wall} :south] [nil :north]))
    (is (g/wall-between? [nil :south] [{:north :wall} :north]))))

(deftest coord-in-direction
  (let [maze (g/->maze 3 3)]
    (testing "returns a coordinate to the north"
      (is (= [2 3] (g/coord-in-direction maze [2 2] :north))))
    (testing "returns a coordinate to the east"
      (is (= [3 2] (g/coord-in-direction maze [2 2] :east))))
    (testing "returns a coordinate to the south"
      (is (= [2 1] (g/coord-in-direction maze [2 2] :south))))
    (testing "returns a coordinate to the west"
      (is (= [1 2] (g/coord-in-direction maze [2 2] :west))))
    (testing "returns nil when there is no coordinate to the north"
      (is (nil? (g/coord-in-direction maze [2 3] :north))))
    (testing "returns nil when there is no coordinate to the east"
      (is (nil? (g/coord-in-direction maze [3 2] :east))))
    (testing "returns nil when there is no coordinate to the south"
      (is (nil? (g/coord-in-direction maze [2 1] :south))))
    (testing "returns nil when there is no coordinate to the west"
      (is (nil? (g/coord-in-direction maze [1 2] :west))))))

(deftest move-cursor
  (testing "changes the cursor value to the passed coordinate"
    (let [maze (g/->maze 2 2)]
      (is (= [1 1]
             (:cursor maze)))
      (is (= [2 2]
             (-> maze (g/move-cursor [2 2]) (:cursor)))))))

(deftest maze->perimeter
  (testing "returns the perimiter of the maze"
    (is (= 10 (g/maze->perimeter (g/->maze 2 3))))))

(deftest maze->facets
  (testing "gets a sequence of pairs of permiteter coordinates and the direction of the edge"
    (is (= '([[1 1] :west]
             [[1 2] :west]
             [[1 3] :west]
             [[1 3] :north]
             [[2 3] :north]
             [[2 3] :east]
             [[2 2] :east]
             [[2 1] :east]
             [[2 1] :south]
             [[1 1] :south])
           (g/maze->facets (g/->maze 2 3))))))

(deftest perimeter-walk->facet
  (testing "walks a maze perimiter and finds the right facet"
    (let [maze (g/->maze 3 3)]
      (is (= [[1 1] :south] (g/perimeter-walk->facet maze 12)))
      (is (= [[1 3] :north] (g/perimeter-walk->facet maze 4)))
      (is (= [[3 2] :east] (g/perimeter-walk->facet maze 8)))
      (is (= [[1 3] :west] (g/perimeter-walk->facet maze 15))))))

(deftest change-edge-type
  (let [maze (g/->maze 3 3)]
    (testing "changes the edge type of a cell in a specified direction"
      (is (= :door (-> maze
                       (g/change-edge-type {:coord [2 2], :direction :north, :edge-type :door})
                       (get-in [:cells [2 2] :north]))))
      (is (= :exit (-> maze
                       (g/change-edge-type {:coord [3 3], :direction :east, :edge-type :exit})
                       (get-in [:cells [3 3] :east])))))))

(deftest add-door
  (testing "adds a door in a given direction on a given cell"
    (is (= :door (-> (g/->maze 3 3)
                     (g/add-door {:coord [2 2], :direction :south})
                     (get-in [:cells [2 2] :south]))))))

(deftest walk-and-add-outlet
  (testing "walks the perimeter and adds an outlet"
    (let [maze (g/->maze 3 3)]
      (is (= :entrance (-> maze
                           (g/walk-and-add-outlet {:steps 5, :outlet-type :entrance})
                           (get-in [:cells [2 3] :north]))))
      (is (= :exit (-> maze
                       (g/walk-and-add-outlet {:steps 7, :outlet-type :exit})
                       (get-in [:cells [3 3] :east])))))))

(deftest wall-and-add-exit
  (testing "walks the perimeter and adds an exit"
    (let [maze (g/->maze 3 3)]
      (is (= :exit (-> maze
                       (g/walk-and-add-exit 7)
                       (get-in [:cells [3 3] :east])))))))

(deftest wall-and-add-entrance
  (testing "walks the perimeter and adds an entrance"
    (let [maze (g/->maze 3 3)]
      (is (= :entrance (-> maze
                           (g/walk-and-add-entrance 10)
                           (get-in [:cells [3 1] :south])))))))

(deftest link-cell
  (testing "links one cell to another with doors"
    (let [maze (g/->maze 3 3)
          linked-maze (g/link-cell maze [3 2] :north)]
      (is (= :door (get-in linked-maze [:cells [3 2] :north])))
      (is (= :door (get-in linked-maze [:cells [3 3] :south]))))))
