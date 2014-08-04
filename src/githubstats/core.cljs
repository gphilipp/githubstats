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
                (go
                  (>! c (-> e .-target .getResponseJson))
                  (close! c))))
    c))

(def res (GET repos))
(go (prn (<! res)))


(om/root
  (fn [app owner]
    (dom/h1 nil (:text app)))
  app-state
  {:target (. js/document (getElementById "stats"))})