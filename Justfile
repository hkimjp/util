# shows available recipies
help:
  just --list

outdated:
  clojure -Sdeps '{:deps {com.github.liquidz/antq {:mvn/version "RELEASE"}}}' -M -m antq.core

upgrade:
  clojure -Tantq outdated :upgrade true

upgrade-force:
  clojure -Tantq outdated :upgrade true :force true

dev:
  clojure -M:dev -m nrepl.cmdline

dev-container:
  clojure -M:dev -m nrepl.cmdline -b 0.0.0.0 -p 7777

run:
  clojure -M:run-m

format_check:
  clojure -M:format -m cljfmt.main check src dev test

format:
  clojure -M:format -m cljfmt.main fix src dev test

lint:
  clojure -M:lint -m clj-kondo.main --lint .

test:
    clojure -M:dev -m kaocha.runner

build:
  clojure -T:build ci

# SERVER:='ubuntu@app.melt.kyutech.ac.jp'
SERVER:='ubuntu@eq.local'
deploy: build
  ssh {{SERVER}} mkdir -p request-map/log
  scp target/io.github.hkimjp/request-map-*.jar {{SERVER}}:request-map/request-map.jar
  rsync -av systemd {{SERVER}}:request-map/
  ssh {{SERVER}} sudo cp request-map/systemd/request-map.service /lib/systemd/system
  ssh {{SERVER}} sudo systemctl daemon-reload
  ssh {{SERVER}} sudo systemctl restart request-map
  ssh {{SERVER}} systemctl status request-map

clean:
  rm -f target/db.sqlite
