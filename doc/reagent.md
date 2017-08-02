# Introduction to reagent



###  working with the DOM

``` clojure
(.-backgroundColor (.-style (js/document.querySelector "#target"))
 (r/render [tit] (js/document.querySelector "#target")))
```
