(ns githubstats.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [weasel.repl :as ws-repl]
            [goog.net.XhrIo :as xhr]
            [cljs.core.async :as async :refer [close! chan >! <! put! take!]]
            [cognitect.transit :as t])
  (:require-macros [cljs.core.async.macros :refer [go alt!]]))

(enable-console-print!)
(ws-repl/connect "ws://localhost:9001" :verbose true)

(def rdr (t/reader :json))


(defn json->clj [j]
  (t/read rdr j))

(def repos "https://api.github.com/users/gphilipp/repos")

(defn GET [url]
  (let [c (chan)]
    (xhr/send url
              (fn [e]
                (let [res (-> e .-target .getResponseText)]
                    (go
                      (>! c res)
                      (close! c)))))
    c))

(defn as-maps [statsfreq]
  (clj->js
    (for [[k v] statsfreq
          :when k]
      {"language used" k "number of repositories" v})))

(defn draw-chart [data]
  (let [svg (.newSvg js/dimple "#stats" 590 400)
        Chart (.-chart js/dimple)]
    (doto (Chart. svg data)
     (.setBounds 60 30 510 305)
     (.addMeasureAxis "y" "number of repositories")
     (.addCategoryAxis "x" "language used")
     (.addSeries nil (-> js/dimple .-plot .-bar))
     .draw)))


(go
  (->> (<! (GET repos))
       json->clj
       (map #(get % "language"))
       (frequencies)
       (as-maps)
       (draw-chart)))

#_(def app-state (atom {:text "Hello world!"}))

#_(om/root
  (fn [app owner]
    (dom/h1 nil (:text app)))
  app-state
  {:target (. js/document (getElementById "stats"))})
