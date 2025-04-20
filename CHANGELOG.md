## Unreleased

* avoid inline def.
* separate benchmark/datascript/util?

## 0.3.1 (2025-04-19)

* did now work in micro-x.

## 0.3.0 (2025-04-19)

* (start) ... on-memory mode
* (start "db") ... backend sqlite mode. "db" is the path of sqlite db saved.

## 0.2.5 (2025-04-19)

* changed `just dev` -> `just repl`, `just dev-container` -> `just container-repl`.

## 0.2.4 (2025-04-18)

* typo, one more.

## 0.2.3 (2025-04-18)

* fix typo

## 0.2.2 (2025-04-18)

* added datascript: (put fact) and (puts facts)
* changed datascript log level from :info to :debug.

## 0.2.1 (2025-03-31)

* added - org.slf4j/slf4j-simple  {:mvn/version "2.0.17"}
* warn if `target/db.sqlite` does not exist.
* log folder?
* BRAKING - datascript/create, restore takes argument `db`, such as "target/db.sqlite".
* updated create, restore - if no db given, choose `target/db.sqlite`.

## 0.2.0 (2025-03-25)

## 0.1.0 (2025-03-25)

* successed `just build`.
