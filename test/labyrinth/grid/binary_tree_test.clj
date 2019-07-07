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
