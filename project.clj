(defproject hello-browser "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [ [org.clojure/clojure "1.9.0-alpha12"]
                  [org.clojure/clojurescript "1.9.671"]
                  [expound "0.1.1-SNAPSHOT"]
                  ;[orchestra "2017.07.04-1"]]
                  [org.clojure/test.check "0.10.0-alpha2"]]

  :plugins [[lein-figwheel "0.5.11"]]
  :clean-targets [:target-path "out"]
  :cljsbuild {
              :builds [{:id "dev"
                        :source-paths ["src"]
                        :figwheel true
                        :compiler {:main "hello.core"}}]})
