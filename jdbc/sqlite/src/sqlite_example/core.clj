(ns sqlite-example.core
  (:require [clojure.java.jdbc :as j]))

(def sqlite-db {:dbtype "sqlite"
                :dbname "my.db"})

(comment
  (let [q (j/create-table-ddl :post [[:id :integer "primary key" :autoincrement]
                                     [:title "varchar(128)"]
                                     [:body :text]]
                              {:conditional? true})]
    (j/execute! sqlite-db
                [q]))

  (j/insert! sqlite-db :post
             {:title "hello" :body "hello world!"})
  
  (j/query sqlite-db
           ["select * from post"])

  (let [q (j/drop-table-ddl :post)]
    (j/execute! sqlite-db
                [q]))
  
  ;; end of comment
  )
