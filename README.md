# Labyrinth [![CircleCI](https://circleci.com/gh/corasaurus-hex/labyrinth.svg?style=svg)](https://circleci.com/gh/corasaurus-hex/labyrinth)

A Clojure library designed to generate mazes.

## Usage

``` clojure
(require '[labyrinth.grid :as g]
         '[labyrinth.grid.export.ir :as ir]
         '[labyrinth.grid.binary-tree :as bt]
         '[labyrinth.grid.export :as e])

(-> (g/->maze 10 10) ;; build an empty 10x10 cell maze
    (bt/gen)         ;; generate the maze using the binary-tree algorithm
    (ir/->ir)        ;; generate an intermediate representation of the maze for exporting
    (e/ir->txt)      ;; take the intermediate representation and convert it to txt
    (println))

;; ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
;; ┃                                                 ┃
;; ┃    ┏━━━━╸    ╻    ╻    ┏━━━━╸    ╻    ┏━━━━╸    ┃
;; ┃    ┃         ┃    ┃    ┃         ┃    ┃         ┃
;; ┗━━━━┻━━━━╸    ┣━━━━┛    ┣━━━━╸    ┃    ┃    ╻    ┃
;;                ┃         ┃         ┃    ┃    ┃    ┃
;; ╻    ┏━━━━╸    ┣━━━━━━━━━┛    ┏━━━━┻━━━━┛    ┃    ┃
;; ┃    ┃         ┃              ┃              ┃    ┃
;; ┣━━━━┛    ┏━━━━┻━━━━╸    ╻    ┃    ╻    ┏━━━━┛    ┃
;; ┃         ┃              ┃    ┃    ┃    ┃         ┃
;; ┣━━━━━━━━━┛    ┏━━━━╸    ┣━━━━┛    ┣━━━━┻━━━━╸    ┃
;; ┃              ┃         ┃         ┃              ┃
;; ┣━━━━━━━━━━━━━━┻━━━━╸    ┣━━━━╸    ┃    ╻    ╻    ┃
;; ┃                        ┃         ┃    ┃    ┃    ┃
;; ┣━━━━╸    ╻    ┏━━━━━━━━━┻━━━━╸    ┣━━━━┻━━━━┛    ┃
;; ┃         ┃    ┃                   ┃              ┃
;; ┃    ┏━━━━┻━━━━┛    ┏━━━━╸    ╻    ┃    ┏━━━━╸    ┃
;; ┃    ┃              ┃         ┃    ┃    ┃         ┃
;; ┃    ┣━━━━╸    ┏━━━━┻━━━━╸    ┣━━━━┛    ┃    ╻    ┃
;; ┃    ┃         ┃              ┃         ┃    ┃    ┃
;; ┗━━━━┻━━━━╸    ┗━━━━━━━━━━━━━━┻━━━━━━━━━┻━━━━┻━━━━┛
```

## License

Copyright © 2019 Cora Sutton

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
