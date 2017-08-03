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

### generating UI
[link video apresentacao](https://www.youtube.com/watch?v=klX8d9A-M94)

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
and render

```clojure
;
(defn ^:export run []
 (r/render [(show srzly??? kiss-ui)]
           (js/document.getElementById "app")))

(run)
```

### 4 data model

``` clojure
(def attrs
   {:person/person {:type :map
                    :keys [:person/firstname
                           :person/lastname
                           :person/birthday
                           :person/deceased]}
    :person/firstname {:type :string
                       :min 1
                       :max 20}
    :person/lastname {:type :string
                      :min 1
                      :max 20}
    :person/birthday {:type :date}
    :person/deceased {:type :date}})
```
example

```clojure
(def a-person (r/atom {:person/person
                        {:person/firstname "Renate"
                         :person/lastname "Chasman"
                         :person/birthday "1932-01-10"}}))
```

``` clojure
(defmulti render* (fn [attrs path data-atom]
  (:type (get attrs (last path)))))

;truque para multimethod funcionar com reagent
(defn render [attrs path data-atom]
  (render* attrs path data-atom))

(defn update! [a path value]
     (swap! a assoc-in path value))

(defmethod render* :string [attrs path data-atom]
  [:div [:label (last path)]
   [:input {:value (get-in @data-atom path)
            :on-change (fn [e]
                         (update! data-atom path (-> e .-target.value)))}]])

(defmethod render* :date [attrs path data-atom]
   [:div [:label (last path)]
    [:input {:type :date
             ;; better: user date type with parse and format
             :value (get-in @data-atom path)
             :on-change (fn [e] (update! data-atom path (-> e .-target.value)))}]])     
             (defmethod render* :map [attrs path data-atom]
               (let [attr (get attrs (last path))]
                 [:fieldset
                   [:legend (last path)]
                   (for [k (:keys attr)]
                     (render attrs (vec (concat path [k])) data-atom))]))                                 
```


```clojure  
(show a-person
      (fn [a]
        (render attrs [:person/person] a)))

(defn ^:export run2 []
   (r/render [(show a-person
               (fn [a]
                 (render attrs [:person/person] a)))]
              (js/document.getElementById "app")))
```


* A model to describe the UI

``` clojure
(def person-ui
   {:input :fieldset
    :fields [{:attr :person/firstname :input :textedit}
             {:attr :person/lastname  :input :textedit}
             {:attr :person/birthday  :input :textedit}]})
```
Add labels

``` clojure

(def person-ui
   {:input :fieldset
    :label "Person"
    :fields [{:path [:person/person :person/firstname] :input :textedit
              :label "Given name"}
             {:path [:person/person :person/lastname] :input :textedit
              :label "Family name"}
             {:path [:person/person :person/birthday] :input :textedit
              :label "Date of birth"}]})

```

```clojure
(defmulti ui-element*
      (fn [{:keys [input] :as ui-definition} data-atom] input))

(defn ui-element [attr data-atom]
       @data-atom ;; force deref. this is a hack... Â¯\_(ãƒ„)_/Â¯
       (ui-element* attr data-atom))  

(defmethod ui-element* :textedit [{:keys [path label]} data-atom]
    [:div [:label (or label (last path))]
      [:input {:value (get-in @data-atom path)
              :on-change (fn [e]
                           (update! data-atom path (-> e .-target.value)))}]])

(defmethod ui-element* :date [{:keys [path label]} data-atom]
    [:div [:label (or label (last path))]
     [:input {:type :date
              ;; again, use proper date type eventually
              :value (get-in @data-atom path)
              :on-change (fn [e] (update! data-atom path (-> e .-target.value)))}]])

(defmethod ui-element* :fieldset [{:keys [path label fields]} data-atom]
     (let [attr (get attrs (last path))]
       [:fieldset
        [:legend (or label (last path))]
        (for [f fields]
          (ui-element f data-atom))]))

(show a-person
        (fn [data-atom] [ui-element person-ui data-atom]))

(defn ^:export run3 []
   (r/render [(show a-person
           (fn [data-atom] [ui-element person-ui data-atom]))]
              (js/document.getElementById "app")))
```
RAndom

```clojure

(defmethod ui-element* :random-string [{:keys [path label choices]} data-atom]
  (let [attr (get attrs (last path))]
    [:div [:label (or label  (last path))]
     [:input {:type :button
              :value "Randomize!"
              :on-click (fn [e]
                          (update! data-atom path (rand-nth choices)))}]]))

```

```clojure

(def city-data (r/atom {:city "Berlin"}))

(def city-ui {:input :fieldset
              :fields [{:input :textedit
                        :label "City Name"
                        :path [:city]}
                       {:input :random-string
                        :label "Randomize it!"
                        :path [:city]
                        :choices ["New York" "Rio" "Tokio"]}]})

(show city-data
      (fn [data-atom]
        [ui-element city-ui data-atom]))


(defn ^:export run4 []
   (r/render [(show city-data
         (fn [data-atom]
           [ui-element city-ui data-atom]))]
              (js/document.getElementById "app")))
```
