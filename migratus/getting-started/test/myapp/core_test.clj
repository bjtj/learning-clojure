(ns myapp.core-test
  (:require [clojure.test :refer [deftest is]]
            [next.jdbc :as j]))

(deftest initial-test
  (is (= 0 (-> (j/execute-one! {:dbtype "h2"
                                :dbname "migratus.db"}
                               ["SELECT COUNT(*) as COUNT FROM `dumb`"])
               :COUNT))))

(deftest create-user-test
  (is (= 0 (-> (j/execute-one! {:dbtype "h2"
                                :dbname "migratus.db"}
                               ["SELECT COUNT(*) as COUNT FROM `user`"])
               :COUNT))))
