(ns corasaurus-hex.labyrinth.grid.export)

(def box-chars
  "Unicode characters for rendering boxes.
  Coincidentally, mazes are made of boxes.

  https://www.unicode.org/charts/PDF/U2500.pdf

  ┏━━━━┳━━━━┳━━━━┳━━━━┳━━━━╸
  ┃    ┃    ┃    ┃    ┃
  ┣━━━━╋━━━━╋━━━━╋━━━━┛    ╻
  ┃    ┃    ┃    ┃         ┃
  ┣━━━━╋━━━━╋━━━━┛    ┏━━━━┫
  ┃    ┃    ┃         ┃    ┃
  ┣━━━━╋━━━━┛    ┏━━━━╋━━━━┫
  ┃    ┃         ┃    ┃    ┃
  ┗━━━━┛    ┏━━━━╋━━━━╋━━━━┫
            ┃    ┃    ┃    ┃
  ╺━━━━━━━━━┻━━━━┻━━━━┻━━━━┛

  Uses heavy characters."
  {:horizontal "━━━"
   :vertical "┃"
   :down-and-right "┏"
   :down-and-left "┓"
   :up-and-right "┗"
   :up-and-left "┛"
   :vertical-and-right "┣"
   :vertical-and-left "┫"
   :down-and-horizontal "┳"
   :up-and-horizontal "┻"
   :vertical-and-horizontal "╋"
   :half-left "╸"
   :half-up "╹"
   :half-right "╺"
   :half-down "╻"
   :vertical-empty " "
   :horizontal-empty "   "
   :empty "   "
   :intersection-horizontal "━"
   :west-entrance ">"
   :west-exit "<"
   :east-entrance "<"
   :east-exit ">"
   :north-entrance " ∨ "
   :north-exit " ∧ "
   :south-entrance " ∧ "
   :south-exit " ∨ "})

(def arrows {:left "<"
             :right ">"
             :down "v"
             :up "^"})

(defn ->txt-coords
  [width height]
  (for [row (range height 0 -1)
        col (range 1 (inc width))]
    [col row]))

(defn ir->txt
  [{:keys [width height blocks]}]
  (reduce (fn [s [col _row :as coord]]
            (str s
                 (cond-> (box-chars (blocks coord))
                   (= col width) (str "\n"))))
          ""
          (->txt-coords width height)))
