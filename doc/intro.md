# Introduction to spec-clojurescrip


# .spec
### Double Colon - Namespaced keywords

Clojure's keywords are defined as a word prefixed with a colon. Words prefixed with two colons are namespaced keywords. If we have a namespace named `'spec.demo'`, a `:word` resolves to `:word`, however a `::word` resolves to `:spec.demo/word`

``` clojure
(ns my.audio.lib
  (:require [transfer :as t]))

;shorthand for a keyword in current namespace
::encoding     ;;my.audio.lib/encoding

;shorthand for a keyword in an aliased namespace

::t/encoding     ;;transfer/encoding

```

### Test.Check
You're a savvy Clojure Developer. You've already added [org.clojure/test.check "0.9.0"] to your dev dependencies and you're using it in your application, but you want to leverage the awesome integration with Clojure's Spec, too. You'll start by requiring [clojure.spec.gen.alpha :as gen] in your namespace.

We can then leverage generation with the same sense of composition as a spec definition.
```  clojure
(ns hello.core)
(require '[cljs.spec.alpha  :as s])
(require '[clojure.test.check :as tc])
(require '[clojure.test.check.generators :as gen])
;(require '[clojure.test.check.properties :as prop])
(require '[clojure.test.check.properties :as prop :include-macros true])
(require '[expound.alpha :as expound]) ;more readble messages than spec/explain

(gen/generate (s/gen int?))
> 612
```

Note: The value will be random.

Lets now define a new spec for a *map* representing a developer.
where skills are optional.

> keys

> Creates and returns a map validating spec. :req and :opt are both
vectors of namespaced-qualified keywords.

``` clojure
(s/def ::name string?)
(s/def ::age int?)
(s/def ::skills list?)

(s/def ::developer (s/keys :req [::name ::age]
                           :opt [::skills]))


```
We can test if a example conforms to the spec
``` clojure

(s/valid? string? "ABC-123")
;true

(s/valid? even? 13)
;false

(s/valid? ::name "ABC-123")
;true

(s/valid? ::developer {::name "Tom" ::age 22})
;true

(s/conform ::developer {::name "Tom" ::age 22})
;=>#:hello.core{:name "Tom", :age 22}

(s/conform ::developer {::name "Tom" ::age "e3"})
;=>:cljs.spec.alpha/invalid

(s/explain-str ::developer {::name "Tom" ::age "e3"})
;=> "In: [:cljs.user/age] val: \"e3\" fails spec: :cljs.user/age at: [:cljs.user/age]
;predicate: int?\nval: #:cljs.user{:name \"Tom\", :age \"e3\"} fails spec: :cljs.user/developer predicate: (contains? % :name)\nval: #:cljs.user{:name \"Tom\", :age \"e3\"} fails spec: :cljs.user/developer predicate: (contains? % :age)\n:cljs.spec.alpha/spec  :cljs.user/developer\n:cljs.spec.alpha/value  #:cljs.user{:name \"Tom\", :age \"e3\"}\n"

(expound/expound ::developer {::name "Tom" ::age "e3"})
; -- Spec failed --------------------
;   {:cljs.user/name ..., :cljs.user/age "e3"}
;                                        ^^^^                                      ^^^^
;   should satisfy
;   int?
; -- Relevant specs -------
;
; :cljs.user/age: cljs.core/int?
; :cljs.user/developer:
;   (cljs.spec.alpha/keys
;    :req-un [:cljs.user/name :cljs.user/age]
;    :opt-un [:cljs.user/skills])
; -------------------------
; Detected 1 error
```
 generate a example that conforms to the spec definition.
```clojure

(gen/generate (s/gen ::developer))
;=> #:hello.core{:spec.demo/name "A1s41l"
;                :spec.demo/age 9134
;                :spec.demo/skills '()}

(s/exercise ::developer)
;.....

```
But if you want JSON data you must un-namespace:

``` clojure
(s/def ::developer (s/keys :req-un [::name ::age]
                                 :opt-un [::skills]))

(s/valid? ::developer {:name "Tom" :age 22})

```
generate a JSON
``` clojure
(gen/generate (s/gen ::developer))
;{:name "5u75SJ4Fl8M8", :age -130186}  

;Generate data from specs:
(gen/sample (s/gen string?))
;=>("" "" "n" "Xp" "5lZj" "UHJ" "h5" "q" "72" "gPC7pN")

(gen/sample (s/gen ::developer))
;=>({:name "", :age -1, :skills ()}
;    {:name "w", :age -1, :skills ()}
;    {:name "M", :age 0, :skills (:!)}
;    {:name "c", :age 0, :skills (-0.6875 :RY)}
;    {:name "uM5", :age -1}
;    {:name "29", :age 1, :skills (-2 true "\"%(")}
;    {:name "XromNN", :age 1, :skills (:Sz.x/+9* -3 true "S/Aw8)")}
;    {:name "2hOiiP", :age -7, :skills (W+1 :v! :+8c T5c)}
;    {:name "Gp3Q58", :age 1}
;    {:name "0VP6l0", :age 27, :skills (-6 "ir9|?f)" G/c :_J r)})                               
```       

## quick-check *(from docs)*:

```clojure

(def sort-idempotent-prop
  (prop/for-all [v (gen/vector gen/int)]
    (= (sort v) (sort (sort v)))))

(tc/quick-check 100 sort-idempotent-prop)
;; => {:result true, :num-tests 100, :seed 1382488326530}

(def prop-no-42
  (prop/for-all [v (gen/vector gen/int)]
    (not (some #{42} v))))
;=> #'cljs.user/prop-no-42

(tc/quick-check 100 prop-no-42)
; => {:result false,
;      :result-data {},
;      :seed 1500655978219,
;      :failing-size 43,
;      :num-tests 44,
;      :fail [[42 -3 17 -28 40 -9 43 -12 -23 28 -31 -4]],
;      :shrunk {:total-nodes-visited 14,
;               :depth 4,
;               :result false,
;               :result-data {},
;               :smallest [[42]]}}


```

## spec + quick-check

Clojure spec Screencast: Testing[video ClojureTV](https://www.youtube.com/watch?v=W6crrbF7s2s)

[![ video](https://img.youtube.com/vi/W6crrbF7s2s/0.jpg)](https://www.youtube.com/watch?v=W6crrbF7s2s)

```clojure
(require '[clojure.string :as str])            

(defn my-index-of
      "funcao indice este texto descreve a funcao para documentacao"
      [source search & opts]
      (apply str/index-of source search opts))

        ; (defn my-index-alt
        ;             "funcao indice"
        ;             [source search]
        ;             (.indexOf source search))


(s/fdef my-index-of
        :args (s/cat :source string? :search string?)
        :ret nat-int?
        :fn #(<= (:ret %) (-> % :args :source count)))

(s/exercise-fn 'my-index-of)
; =>([("" "") 0]
;  [("" "1") nil]
;  [("Q" "") 0]
;  [("X2" "j0") nil]
;  [("" "BB") nil]
;  [("" "") 0]
;  [("" "Mb0n8O") nil]
;  [("hN9e" "7L3BQS") nil]
;  [("0i" "1qd") nil]
;  [("05Rc" "985RtEo7q") nil])


(doc my-index-of)

; -------------------------
; cljs.user/my-index-of
; ([source search & opts])
;   funcao indice
; Spec
;  args: (cat :source string? :search string?)
;  ret: nat-int?
;  fn: (<= (:ret %) (-> % :args :source count))

```



Improve definition using alt in ":search"
``` clojure

;; alt in args
(s/fdef my-index-of
        :args (s/cat :source string?
                     :search (s/alt :string string? :nat number? )
                     :from (s/? keyword?))
        :ret nat-int?
        :fn #(<= (:ret %) (-> % :args :source count)))

(s/exercise-fn 'my-index-of)
; =>([("" "") 0]
;    [("7" "2") nil]
;    [("" "mQ") nil]
;    [("LA" "252") nil]
;    [("" 336) nil]
;    [("6K2" "aV") nil]
;    [("" "3n") nil]
;    [("IURzB" "swB") nil]
;    [("fiXj" "l86O") nil]
;    [("Wqom8" "") 0])
```

```clojure
(require '[clojure.spec.test.alpha :as test])

(test/check `my-index-of)
; => [{:spec #object[cljs.spec.alpha.t_cljs$spec$alpha40632],
;       :clojure.test.check/ret
;       {:result false,
;        :result-data
;        {:clojure.test.check.properties/error
;           #error {:message "Specification-based check failed",
;                   :data {:cljs.spec.alpha/problems [{:path [:ret], :pred (cljs.core/fn [%] (cljs.core/nat-int? %)), :val nil, :via [], :in []}], :cljs.spec.alpha/spec #object[cljs.spec.alpha.t_cljs$spec$alpha40332], :cljs.spec.alpha/value nil, :cljs.spec.test.alpha/args ("" -2), :cljs.spec.test.alpha/val nil, :cljs.spec.alpha/failure :check-failed}}},
;        :seed 1500855216597,
;        :failing-size 0,
;        :num-tests 1,
;        :fail [("" -2)],
;        :shrunk
;        {:total-nodes-visited 7,
;         :depth 5,
;         :result false,
;         :result-data
;         {:clojure.test.check.properties/error
;          #error {:message "Specification-based check failed", :data {:cljs.spec.alpha/problems [{:path [:ret], :pred (cljs.core/fn [%] (cljs.core/nat-int? %)), :val nil, :via [], :in []}], :cljs.spec.alpha/spec #object[cljs.spec.alpha.t_cljs$spec$alpha40332], :cljs.spec.alpha/value nil, :cljs.spec.test.alpha/args ("" 0 :A), :cljs.spec.test.alpha/val nil, :cljs.spec.alpha/failure :check-failed}}},
;         :smallest [("" 0 :A)]}},
;       :sym cljs.user/my-index-of,
;       :failure false}]

;to get a summary of the result
(->> (test/check `my-index-of) test/summarize-results)
; =>  {:total 1, :check-threw 1}
;     dev:cljs.user=> {:sym cljs.user/my-index-of, :failure false}

```
modify the return
``` clojure
(s/fdef my-index-of
        :args (s/cat :source string?
                     :search (s/alt :string string? :nat number? )
                     :from (s/? keyword?))
        :ret (s/nilable nat-int?)
        :fn #(<= (:ret %) (-> % :args :source count)))

(test/check `my-index-of)

[{:spec #object[cljs.spec.alpha.t_cljs$spec$alpha40632],
  :clojure.test.check/ret
  {:result true, :num-tests 1000, :seed 1500855425670},
  :sym cljs.user/my-index-of}]

(->> (test/check `my-index-of) test/summarize-results)
  {:total 1, :check-passed 1}
```
But this does not work in clojure you would need:
``` clojure
(s/fdef my-index-of
        :args (s/cat :source string?
                     :search (s/alt :string string?
                                    :char char?)
                     :from (s/? nat-int?))
        :ret (s/nilable nat-int?)
        :fn (s/or
              :not-found #(nil? (:ret %))
              :found #(<= (:ret %) (-> % :args :source count))))
```
## clojure != clojurescript

``` clojure

;clojurescript
(<= nil 4)
true
;clojure
(<= nil 4)
;=>NullPointerException   clojure.lang.Numbers.ops (Numbers.java:1013)

```

## which came first

``` clojure
(defn which-came-first
  "Returns :chicken of :egg, depending on which string appears
  first in s, starting from position from."
  [s from]
  (let [c-idx (my-index-of s "chicken" :from from)
        e-idx (my-index-of s "egg" :from from)]
    (cond
      (< c-idx e-idx) :chicken
      (< e-idx c-idx) :egg)))

;; Stacktrace Assisted Debugging
(which-came-first "the chicken or the egg" 0)
;nao funciona com clojurescript
;(clojure.repl/pst)

;; instrumentation
(test/instrument `my-index-of)
(which-came-first "the chicken or the egg" 0)

;; test + instrumentation
(s/fdef which-came-first
        :args (s/cat :source string? :from nat-int?)
        :ret #{:chicken :egg})
```
Clojure spec Screencast Series [videos](https://www.youtube.com/watch?v=WoFkhE92fqc&t=90s)


## generating

from a spec presentation [video](https://www.youtube.com/watch?v=S43Y9a876K8&t=756s)
[code](https://gist.github.com/mrcnc/44a0257818f8932085f398ca20abe7ba)
``` clojure
(gen/sample  (gen/not-empty gen/string-alphanumeric) )
;=>("x" "6" "Se" "SF" "Uk1a" "y" "4KtpR" "P3FJ" "9bX" "DPU")

(gen/sample gen/int 20)
;=>(0 -1 -2 0 2 4 4 -5 -3 -1 9 4 -10 -8 -12 0 11 16 -8 -8)

(gen/sample  gen/string-alphanumeric)
;=>("" "f" "" "" "S" "pR" "iYwe" "zVt6Z9J" "M" "301Tz5")

(gen/sample (gen/map gen/keyword gen/boolean) 5)
;=>({} {:* true} {} {:!9:!* true, :V+ false, :+h false} {})
(gen/sample (gen/map gen/keyword gen/boolean) 5)
;=>({} {:T! false} {:N:V true} {:hf false} {:h:- false, :? false})

(gen/generate gen/string-alphanumeric)
;=>"UQXM5WQ5o"

(gen/sample gen/string-alphanumeric)
;=>("" "5" "bE" "B" "r4Bc" "T6zax" "70u8c" "FU9RNf" "J" "4Jwv66KDE")

(gen/generate gen/any)
;=>#{}

(gen/generate gen/any)
;=>[[-951 "s" :N:-?G:I8nm:2:!!]]

(gen/generate gen/any)
;=> {[1.75] #{:s6:u*92 "¶ªJô»-1#ngAÄM*ã| "},() :_?*-b.UB.oh+-u.vV6a?/e*, #{:q?+P.GZ4.k.?/aa**l"0¯ÃãÆûñ£{wF·.Gó¿çéýÎ®Ë'5ý&#"} (-789), true {}}

```
## user spec example

``` clojure
;; here's how we might spec a user
(def email-regex #"[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}")
(s/def ::user/email (s/and string? #(string? (re-matches email-regex %))))
(s/def ::user/password string?)
(s/def ::user/first-name string?)
(s/def ::user/last-name string?)
;; spec a map with required and optional keys
(s/def ::user-spec
  (s/keys :req-un [::user/email ::user/password]
          :opt-un [::user/first-name ::user/last-name]))
;; if a spec is invalid you can get the reason why with s/explain
(def user {:first-name "Marc"
           :last-name "Cenac"
           :email "mcenac@boundlessgeo.com"})
(s/explain ::user-spec user)
(s/valid? ::user-spec (assoc user :password "testpass"))

```
But cant generate a exemple:
``` clojure
(gen/generate (s/gen ::user-spec))
;=>#error {:message "Couldn't satisfy such-that predicate after 100 tries.", ...
```

## links
- [informal guide to clojure-spec](http://www.bradcypert.com/an-informal-guide-to-clojure-spec/)
- [generators intro](https://github.com/clojure/test.check/blob/master/doc/intro.md)
