(defproject
  githubstats "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2322"]
                 [org.clojure/core.async "0.1.338.0-5c5012-alpha"]
                 [om "0.6.2"]
                 [com.cognitect/transit-cljs "0.8.161"]
                 [weasel "0.3.0"]
                 [com.cemerick/piggieback "0.1.3"]]

  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :plugins [[lein-cljsbuild "1.0.3"]]

  :source-paths ["src"]

  :cljsbuild { 
    :builds [{:id "githubstats"
              :source-paths ["src"]
              :compiler {
                :output-to "githubstats.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}]})
