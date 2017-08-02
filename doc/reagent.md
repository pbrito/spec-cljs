# Introduction to reagent



###  working with the DOM

``` clojure
(.-backgroundColor (.-style (js/document.querySelector "#target"))

(r/render [tit] (js/document.querySelector "#target")))


(defn render-simple []
  (reagent/render-component [input-field]
                            (.-body js/document))


(js/document.querySelector "#app")
;=>#object[HTMLDivElement [object HTMLDivElement]]
```
``` clojure
(require '[reagent.core  :as r])

(defn greeting [message]
  [:h1 message])

(defn simple-example []
  [:div
   [greeting "Hello world, it is now"]])         
```

``` clojure
(defn ^:export run []
 (r/render [simple-example]
           (js/document.getElementById "app")))
```
``` clojure         
hello.core.run()

```
``` clojure
(run)
```
