(ns hui-example.core
  (:require [io.github.humbleui.ui :as ui]))

(def *show-modal (ui/signal false))
(def *counter (ui/signal 0))

(defn open-modal! []
  (reset! *show-modal true))

(defn close-modal! []
  (reset! *show-modal false))

(ui/defcomp app []
  [ui/stack
   [ui/column
    ^:stretch
    [ui/row
     [ui/padding
      {:padding 10}
      [ui/column
       [ui/label "Humble UI Test"]]]
     [ui/gap {:width 10}]
     ^:stretch
     [ui/center
      [ui/column {:align :center}
       [ui/label "Hello, world"]
       [ui/gap {:height 10}]
       [ui/button
        {:on-click (fn [& args]
                     (open-modal!))}
        "Open Modal"]
       [ui/gap {:height 10}]
       [ui/label @*counter]
       [ui/gap {:height 10}]
       [ui/button
        {:on-click (fn [& args]
                     (swap! *counter inc))}
        "Count Up"]]]]]

   (when @*show-modal
     [ui/overlay
      [ui/size {:width #(:width %)
                :height #(:height %)}
       [ui/padding {:top 10 :right 10 :bottom 10 :left 10}
        [ui/shadow {:dy 2 :blur 10, :color 0x20000000}
         [ui/shadow {:dy 0 :blur 4 :color 0x10000000}
          [ui/rect {:paint {:fill "FD2"}}
           [ui/gap
            [ui/center
             [ui/column {:align :center}
              [ui/label "Hello!"]
              [ui/gap {:height 10}]
              [ui/button {:on-click (fn [& args] (close-modal!))}
               "Close"]]]]]]]]]])])
  
(defn -main [& args]
  (ui/start-app!
    (ui/window #'app)))

