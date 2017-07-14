(ns hello.core
  (:require
        [cljs.spec :as s]))

;(set! *warn-on-reflection* true)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(.log js/console "Hey Seymore sup?!")

(.log js/console  (s/valid? even? 10)) ;; true

(.log js/console (s/valid? even? 10)) ;; true
(.log js/console  (s/valid? #{:club :diamond :heart :spade} :club)) ;; true
(.log js/console  (s/explain-data even? 101))

(s/def ::suit #{:club :diamond :heart :spade})
(.log js/console  (s/explain-data ::suit :cluba))
(.log js/console  "valid string")
(.log js/console  (s/valid? string? "abc"))
(s/def ::big-even (s/and int? even? #(> % 1000)))
; (.log js/console "big int")
; (.log js/console (s/valid? ::big-even 100000))
; (.log js/console (s/valid? ::big-even "100000"))
;
; (s/def ::name-or-id (s/or :name string?
;                           :id   int?))
; ;(.log js/console (s/conform ::name-or-id 100))
; (.log js/console (s/explain ::name-or-id :foo))
