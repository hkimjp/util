# hkimjp/util

My first clojure library. under construcion.

## Under construction

## Usage

* hkimjp.util
* hkimjp.benchmark
* hkimjp.datascript

Add this to deps.edn:

    io.github.hkimjp/util {:git/tag "0.2.0", :git/sha "6ab6980"}

then,

```clojure
(require '[hkimjp.util :as u]
         '[hkimjp.datascript :as d]
         '[hkimjp.benchmark :a b])
```

Run the project directly, via `:exec-fn`:

    $ clojure -X:run-x
    Hello, Clojure!

Run the project's tests (they'll fail until you edit them):

    $ clojure -T:build test

Run the project's CI pipeline and build an uberjar (this will fail until you edit the tests to pass):

    $ clojure -T:build ci

This will produce an updated `pom.xml` file with synchronized dependencies inside the `META-INF`
directory inside `target/classes` and the uberjar in `target`. You can update the version (and SCM tag)
information in generated `pom.xml` by updating `build.clj`.

If you don't want the `pom.xml` file in your project, you can remove it. The `ci` task will
still generate a minimal `pom.xml` as part of the `uber` task, unless you remove `version`
from `build.clj`.

Run that uberjar:

    $ java -jar target/net.clojars.hkimjp/util-0.1.0-SNAPSHOT.jar

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2025 Hkim

Distributed under the Eclipse Public License version 1.0.
