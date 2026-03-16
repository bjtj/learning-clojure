(ns sqlite-example.core
  (:require [next.jdbc :as j]))

(def sqlite-db {:dbtype "sqlite"
                :dbname "my.db"})

(comment
  (let [q "create table if not exists post (
id integer primary key autoincrement,
title varchar(128),
body text)"]
    (j/execute-one! sqlite-db
                    [q]))

  (j/execute-one! sqlite-db
                  ["insert into post(title, body) values ('hello', 'hello world!')"])
  
  (j/execute! sqlite-db
              ["select * from post"])

  (j/execute-one! sqlite-db
                  ["select 1"])

  (let [q "drop table post"]
    (j/execute-one! sqlite-db
                    [q]))
  
  ;; end of comment
  )
