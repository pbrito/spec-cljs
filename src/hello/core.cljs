(ns hello.core
  (:require
      [clojure.browser.repl :as repl]
      ;[cljs.repl :as repl]
      [cljs.spec.alpha :as s]
      [expound.alpha :as expound];prety print messagems
      [clojure.test.check :as tc]
      [reagent.core :as r]

      [clojure.test.check.generators :as gen]
      [clojure.test.check.properties :as prop :include-macros true]))
      ;[orchestra-cljs.spec.test :as st]))

;(set! *warn-on-reflection* true)

;
(s/def :hello.place/city string?)
(s/def :hello.place/state string?)
(s/def :hello/place (s/keys :req-un [:hello.place/city :hello.place/state]))

(def expl1 (s/explain :hello/place {}))
;(expound/expound :hello/place {})


(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn my-inc [x]
  (inc x))

; (s/fdef my-inc
;     :args (s/cat :x number?)
;     :ret number?)
;
; (.log js/console "Hey Seymore sup?!")
;
; (.log js/console  (s/valid? even? 10)) ;; true
;
; (.log js/console (s/valid? even? 10)) ;; true
; (s/valid? even? 13)
;(s/valid? integer? 5)
;(gen/generate (s/gen integer?))

(defn my-index
            "funcao indice"
            [source search]
            "oo")

(s/def ::index-of-args (s/cat :source string? :search string?))
;(.log js/console  (s/explain ::index-of-args ["foo" 8]))
(s/fdef my-index
              :args (s/cat :source string? :search string?)
              :ret nat-int?
              :fn #(<= (:ret %) (-> % :args :source count)))
;(.log js/console (repl/doc my-index))
(.log js/console    "----------------")
(.log js/console(my-index "2" 2))

(def sort-idempotent-prop
  (prop/for-all [v (gen/vector gen/int)]
    (= (sort v) (sort (sort v)))))

(def rr (tc/quick-check 100 sort-idempotent-prop))
;(.log js/console  rr)
;
; (s/def ::name-or-id (s/or :name string?
;                           :id   int?))
; ;(.log js/console (s/conform ::name-or-id 100))
; (.log js/console (s/explain ::name-or-id :foo))


; (ns hello.core)
; (require '[cljs.spec.alpha  :as s])
; (require '[expound.alpha :as expound])
; (s/def :example.place/city string?)
; (s/def :example.place/state string?)
; (s/def :example/place (s/keys :req-un [:example.place/city :example.place/state]))
; (s/explain :example/place {})
; (expound/expound :example/place {})



;;;
;;;
