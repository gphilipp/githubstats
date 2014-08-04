(ns githubstats.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [weasel.repl :as ws-repl]
            [goog.net.XhrIo :as xhr]
            [cljs.core.async :as async :refer [close! chan >! <! put! take!]])
  (:require-macros [cljs.core.async.macros :refer [go alt!]]))

(enable-console-print!)
(ws-repl/connect "ws://localhost:9001" :verbose true)

(def app-state (atom {:text "Hello world!"}))

(def repos "https://api.github.com/users/gphilipp/repos")

(defn GET [url]
  (let [c (chan)]
    (xhr/send url
              (fn [e]
                (let [res (-> e .-target .getResponseJson)]
                    (go
                      (>! c res)
                      (close! c)))))
    c))

(def channel (GET repos))
(go
  (def stats (<! channel)))

(def statsclj (js->clj stats))
(count statsclj)

(def statsfreq (frequencies (map #(get % "language") statsclj)))

(def data (clj->js
            (for [[k v] statsfreq
                  :when k]
              {"lang" k "count" v})))





;var svg = dimple.newSvg("#chartContainer", 590, 400);
;d3.tsv("/data/example_data.tsv", function (data) {
;var myChart = new dimple.chart(svg, data);
;myChart.setBounds(60, 30, 510, 305)
;var x = myChart.addCategoryAxis("x", "Month");
;x.addOrderRule("Date");
;myChart.addMeasureAxis("y", "Unit Sales");
;myChart.addSeries(null, dimple.plot.bar);
;myChart.draw();

(let [svg (.newSvg js/dimple "#stats" 590 400)
      ;data (clj->js (for [[k v] statsfreq]
      ;                {"lang" k "count" v}))
      chart (.-chart js/dimple)
      my-chart (chart. svg data)
      _ (.setBounds my-chart 60 30 510 305)
      _ (.addCategoryAxis my-chart "x" "lang")
      _ (.addMeasureAxis my-chart "y" "count")
      _ (.addSeries my-chart nil (-> js/dimple .-plot .-bar))]
  (.draw my-chart))

#_(om/root
  (fn [app owner]
    (dom/h1 nil (:text app)))
  app-state
  {:target (. js/document (getElementById "stats"))})
