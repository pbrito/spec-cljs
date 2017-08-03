# Introduction to reagent



##  Interacting with JavaScript and the DOM

#### METHOD CALLS

(.method object params)

(.log js/console "hello world!")

#### ACCESSING PROPERTIES

(.-property object)

(.-style div)

``` clojure


(js/document.querySelector "#barra")
;=>#object[HTMLDivElement [object HTMLDivElement]]

(.-backgroundColor (.-style (js/document.querySelector "#barra")))
(set! (.-backgroundColor (.-style (js/document.querySelector "#barra" )))"#234567")

```
### reagent

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
call the function in the browser
``` clojure         
hello.core.run()
```
or call the function in the repl
``` clojure
(run)
```


```clojure

(require '[reagent.core :as r])
(require  '[clojure.pprint :refer [pprint]])

(defn show [data-atom & fs]
  (fn []
    (vec (conj (into [:div.row.show]
                     (for [f fs]
                       [:div.col
                        (f data-atom)]))
               [:div.col
                [:span {:font-weight :bold} "Data Atom"]
                [:code [:pre {:style {:white-space :wrap}}
                        (with-out-str (pprint @data-atom))]]]))))


(def srzly???
  (r/atom (with-out-str
            (pprint {:firstname "John"
                     :lastname "Silly"}))))

(defn kiss-ui [a]
 [:textarea {:value @a
             :on-change (fn [e] (reset! a (.-target.value e)))}])


(show srzly??? kiss-ui)

```
