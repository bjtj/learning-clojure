# Migratus example

<https://github.com/yogthos/migratus>

## Initialize

``` shell
clj -M:migrate init
```

- migrations/init.sql

## Migration

``` shell
clj -M:migrate migrate
```

## Create migration

``` shell
clj -M:migrate create create-user-table
```
