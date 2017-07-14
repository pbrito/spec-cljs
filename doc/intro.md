# Introduction to spec-clojurescrip

TODO: write [great documentation](http://jacobian.org/writing/what-to-write/)

# .spec
### Double Colon

Clojure's keywords are defined as a word prefixed with a colon. Words prefixed with two colons are namespaced keywords. If we have a namespace named `'spec.demo'`, a `:word` resolves to `:word`, however a `::word` resolves to `:spec.demo/word`

### Test.Check
You're a savvy Clojure Developer. You've already added [org.clojure/test.check "0.9.0"] to your dev dependencies and you're using it in your application, but you want to leverage the awesome integration with Clojure's Spec, too. You'll start by requiring [clojure.spec.gen.alpha :as gen] in your namespace.

We can then leverage generation with the same sense of composition as a spec definition.
```
(gen/generate (spec/gen int?))
> 612
```

Note: The value with be random.
```
(gen/generate (spec/gen ::developer))
> {:spec.demo/name "A1s41l"
   :spec.demo/age 9134
   :spec.demo/skills '()}
```
## links
[informal guide to clojure-spec](http://www.bradcypert.com/an-informal-guide-to-clojure-spec/)
