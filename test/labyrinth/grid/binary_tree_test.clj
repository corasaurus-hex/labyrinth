(ns labyrinth.grid.binary-tree-test
  (:require [labyrinth.grid.binary-tree :as b]
            [clojure.test :refer [deftest testing is are]]
            [labyrinth.grid.specs]
            [clojure.spec.alpha :as s]
            [labyrinth.grid :as g]
            [meander.match.delta :as m]))

(deftest random-direction
  (testing "returns a north or an east"
    (is (= '(:east :north)
           (->> b/random-direction
                (repeatedly)
                (take 1000)
                (distinct)
                (sort))))))

(deftest end-coord?
  (testing "returns true when the coord is the end coord"
    (is (b/end-coord? (g/->maze 3 3) [3 3])))
  (testing "returns false when the coord is not the end coord"
    (is (not (b/end-coord? (g/->maze 3 3) [1 3])))))

(deftest at-end?
  (testing "returns true when the cursor is the northwestern-most coordinate"
    (is (-> (g/->maze 3 3)
            (g/move-cursor [3 3])
            (b/at-end?))))
  (testing "returns false when the cursor is not at the northwestern-most coordinate"
    (is (not (-> (g/->maze 3 3)
                 (g/move-cursor [2 3])
                 (b/at-end?))))))

(deftest next-cursor-pos
  (testing "returns the next cursor position in the grid when there is one"
    (is (= [2 1] (b/next-cursor-pos (g/->maze 3 3))))
    (is (= [1 2] (b/next-cursor-pos (g/move-cursor (g/->maze 3 3) [3 1]))))
    (is (= [1 3] (b/next-cursor-pos (g/move-cursor (g/->maze 3 3) [3 2]))))
    (is (= [3 3] (b/next-cursor-pos (g/move-cursor (g/->maze 3 3) [2 3]))))
    (is (= [1 3] (b/next-cursor-pos (g/move-cursor (g/->maze 1 3) [1 2]))))
    (is (= [3 1] (b/next-cursor-pos (g/move-cursor (g/->maze 3 1) [2 1])))))
  (testing "returns nil when there is no next cursor position in the grid"
    (is (nil? (b/next-cursor-pos (g/move-cursor (g/->maze 3 3) [3 3]))))
    (is (nil? (b/next-cursor-pos (g/move-cursor (g/->maze 1 3) [1 3]))))
    (is (nil? (b/next-cursor-pos (g/move-cursor (g/->maze 3 1) [3 1]))))))

(deftest penultimate?
  (testing "returns true when at the coordinate before the end coordinate"
    (is (b/penultimate? (g/move-cursor (g/->maze 3 3) [2 3])))
    (is (b/penultimate? (g/move-cursor (g/->maze 1 3) [1 2])))
    (is (b/penultimate? (g/move-cursor (g/->maze 3 1) [2 1]))))
  (testing "returns false when not at the coordinate before the end coordinate"
    (is (not (b/penultimate? (g/move-cursor (g/->maze 3 3) [3 3]))))
    (is (not (b/penultimate? (g/move-cursor (g/->maze 3 3) [1 3]))))
    (is (not (b/penultimate? (g/move-cursor (g/->maze 1 3) [1 3]))))
    (is (not (b/penultimate? (g/move-cursor (g/->maze 1 3) [1 1]))))
    (is (not (b/penultimate? (g/move-cursor (g/->maze 3 1) [3 1]))))
    (is (not (b/penultimate? (g/move-cursor (g/->maze 3 1) [1 1]))))))

(deftest next-direction-to-link
  (testing "returns :east when at the max row"
    (is (= :east (b/next-direction-to-link (g/move-cursor (g/->maze 3 3) [1 3]))))
    (is (= :east (b/next-direction-to-link (g/move-cursor (g/->maze 3 3) [2 3])))))
  (testing "returns :north when at the max col"
    (is (= :north (b/next-direction-to-link (g/move-cursor (g/->maze 3 3) [3 2]))))
    (is (= :north (b/next-direction-to-link (g/move-cursor (g/->maze 3 3) [3 1])))))
  (testing "returns a random direction, either east or north"
    (let [maze (g/move-cursor (g/->maze 3 3) [2 2])]
      (is (= '(:east :north)
             (->> #(b/next-direction-to-link maze)
                  (repeatedly)
                  (take 1000)
                  (distinct)
                  (sort)))))))

(deftest next-steps
  (let [maze (g/->maze 3 3)]
    (testing "returns nil when at the end of the maze"
      (is (nil? (b/next-steps (g/move-cursor maze [3 3])))))
    (testing "returns a link and a move when before the penultimate position of the maze"
      (let [steps (b/next-steps maze)
            [link move] steps
            link-msg (s/explain-str :binary-tree/link-step link)
            move-msg (s/explain-str :binary-tree/move-step move)]
        (is (= "Success!\n" link-msg) link-msg)
        (is (= "Success!\n" move-msg) link-msg)
        (is (= 2 (count steps)))))
    (testing "returns a link, a move, and an add-outlets step when on the penultimate position of the maze"
      (let [steps (b/next-steps (g/move-cursor maze [2 3]))
            [link move add-outlets] steps
            link-msg (s/explain-str :binary-tree/link-step link)
            move-msg (s/explain-str :binary-tree/move-step move)
            add-outlets-msg (s/explain-str :binary-tree/add-outlets-step add-outlets)]
        (is (= "Success!\n" link-msg) link-msg)
        (is (= "Success!\n" move-msg) link-msg)
        (is (= "Success!\n" add-outlets-msg) add-outlets-msg)
        (is (= 3 (count steps)))))))

(deftest add-outlets
  (testing "adds an entrance and an exit"
    (let [maze (b/add-outlets (g/->maze 10 10))
          entrances (m/search maze {:cells {?coord {?direction :entrance}}} [?coord ?direction])
          exits (m/search maze {:cells {?coord {?direction :exit}}} [?coord ?direction])]
      (is (= 1 (count entrances)))
      (is (= 1 (count exits))))))

(deftest do-step
  (testing "links cells"
    (let [maze (b/do-step (g/->maze 10 10) [:link [[2 2] :north]])]
      (is (= :door (get-in maze [:cells [2 2] :north])))
      (is (= :door (get-in maze [:cells [2 3] :south])))))
  (testing "moves the cursor"
    (let [maze (b/do-step (g/->maze 10 10) [:move [5 5]])]
      (is (= [5 5] (:cursor maze)))))
  (testing "adds outlets"
    (let [maze (b/do-step (g/->maze 10 10) [:add-outlets])
          entrances (m/search maze {:cells {?coord {?direction :entrance}}} [?coord ?direction])
          exits (m/search maze {:cells {?coord {?direction :exit}}} [?coord ?direction])]
      (is (= 1 (count entrances)))
      (is (= 1 (count exits))))))
