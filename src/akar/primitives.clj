(ns akar.primitives
  (:require [akar.internal.utilities :refer [variadic-reducive-function]]))

; A pattern is a function that from one argument to either a sequence of extracted values or `nil`.
; `nil` indicates that the pattern match did not succeed.
; Empty sequence would mean that the pattern matched, but no values were extracted.

; A clause is a function that accepts a pattern p and a function f, and returns a function that
; invokes f with p's matches, when p matches.

(def clause-not-applied
  ::clause-not-applied)

(defn clause-applied? [result]
  (not= clause-not-applied result))

(defn clause [pattern f]
  (fn [arg]
    (if-let [matches (pattern arg)]
      (apply f matches)
      clause-not-applied)))

; A function to compose multiple clauses into one clause.

(def or-else
  (variadic-reducive-function
    :zero (constantly clause-not-applied)
    :combine (fn [clause1 clause2]
               (fn [arg]
                 (let [result (clause1 arg)]
                   (if (clause-applied? result)
                     result
                     (clause2 arg)))))))

; Sugar for defining a pattern matching block.

(defn clauses [& args]
  (->> args
       (partition 2)
       (map (partial apply clause))
       (apply or-else)))

; Functions to match a value against a pattern matching clause.

(defn try-match [value clause']
  (clause' value))

(defn match [value clause']
  (let [result (try-match value clause')]
    (if (clause-applied? result)
      result
      (throw (RuntimeException. (str "Match error: " value))))))
