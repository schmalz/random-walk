(ns random-walk.core
  (:require [clojure.pprint :as p]
            [quil.core :as q]
            [quil.middleware :as m]))

;; Solarized Accent Colours (HSB values).
(def yellow  [45 100 71])
(def orange  [18 89 80])
(def red     [1 79 86])
(def magenta [331 74 83])
(def violet  [237 45 77])
(def blue    [205 82 82])
(def cyan    [175 74 63])
(def green   [68 100 60])

(defrecord Walker [t x y colour alpha])

(defn- step
  [{:keys [t x y] :as walker}]
  (let [jump? (= (rand-int 1000) 0)]
    (-> walker
      (assoc :t (+ t 0.01))
      (assoc :x (+ x (* (dec (rand-int 3)) (if jump? 30 1))))
      (assoc :y (+ y (* (dec (rand-int 3)) (if jump? 30 1))))
      (assoc :alpha (q/map-range (q/noise t) 0 1 0 100)))))

(defn- update-walkers
  [walkers]
  (map step walkers))

(defn- draw
  [{:keys [x y colour alpha]}]
  (apply q/stroke (conj colour alpha))
  (q/point x y))

(defn- draw-walkers
  [walkers]
  (doseq [walker walkers] (draw walker)))

(defn- new-walker
  [t colour]
  (->Walker t (q/random (q/width)) (q/random (q/height)) colour (q/map-range (q/noise t) 0 1 0 100)))

(defn- setup
  []
  (q/smooth)
  (q/color-mode :hsb 360 100 100)
  (q/background 193 100 21) ; Solarized base03.
  [(new-walker 0 violet)
   (new-walker 1.37 blue)
   (new-walker 3.79 cyan)
   (new-walker 5.83 magenta)])

(defn- save-frame-to-disk
  [state _]
  (q/save-frame (p/cl-format nil
                             "frames/~d-~2,'0d-~2,'0d-~2,'0d-~2,'0d-~2,'0d-####.jpeg"
                             (q/year) (q/month) (q/day) (q/hour) (q/minute) (q/seconds)))
  state)

(q/defsketch random-walk
  :title "Random Walk"
  :setup setup
  :draw draw-walkers
  :update update-walkers
  :mouse-clicked save-frame-to-disk
  :middleware [m/fun-mode]
  :size [900 900])
