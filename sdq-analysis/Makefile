DATABASE_FILE=sdq-analysis.db

run: copy-ui run-service

build-jar: copy-ui
	./gradlew bootJar

run-service:
	./gradlew bootRun --args='--database-file=$(DATABASE_FILE)'

copy-ui: build-ui
	rm -rf src/main/resources/static && cp -R sdq-ui/dist src/main/resources/static

build-ui:
	npm run build --prefix ./sdq-ui/

clear-db:
	rm $(DATABASE_FILE)

connect-db:
	sqlite3 $(DATABASE_FILE)