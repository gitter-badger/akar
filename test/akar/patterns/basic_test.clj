(ns akar.patterns.basic-test
  (:require [akar.patterns.basic :refer :all]
            [akar.primitives :refer :all]
            [akar.combinators :refer :all]
            [clojure.test :refer :all]))

(deftest simple-patterns-test

  (testing "!any"
    (is (= :success
           (match :random-value (clauses
                                  !any (fn [] :success))))))

  (testing "!pfail"
    (is (= nil
           (try-match :some-value (clauses
                                    !fail (fn [] :success))))))

  (testing "!var"
    (is (= :some-value
           (match :some-value (clauses
                                !var (fn [x] x))))))

  (testing "!pred"
    (let [!even (!pred even?)
          !odd (!pred odd?)
          block (clauses
                  !even (fn [] :even)
                  !odd (fn [] :odd))]
      (is (= :odd
             (match 9 block)))
      (is (= :even
             (match 8 block)))))

  (testing "!cst"
    (let [block (clauses
                  (!cst 4) (fn [] :fier)
                  (!cst 5) (fn [] :fünf))]
      (is (= :fier
             (match 4 block)))
      (is (= :fünf
             (match 5 block)))))

  (testing "!view"
    (let [block (clauses
                  (!view inc) (fn [x] x))]
      (is (= 10
             (block 9))))))
