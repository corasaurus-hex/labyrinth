(ns labyrinth.grid.binary-tree-test
  (:require [labyrinth.grid.binary-tree :as b]
            [clojure.test :refer [deftest testing is are]]
            [labyrinth.grid :as g]))

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
