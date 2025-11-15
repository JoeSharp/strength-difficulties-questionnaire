DB_MODULE=sdq-database
APP_MODULE=spring-app
UI_MODULE=sdq-ui
DATABASE_FILE=$(DB_MODULE)/data/sdq-analysis.db

run: copy-ui run-service

build-jar: copy-ui
	./gradlew :spring-app:bootJar

run-service:
	./gradlew :spring-app:bootRun --args='--database-file=../$(DATABASE_FILE)'

clean-run: build-db run

copy-ui: build-ui
	rm -rf $(APP_MODULE)/src/main/resources/static && cp -R sdq-ui/dist $(APP_MODULE)/src/main/resources/static

build-ui:
	npm run build --prefix $(UI_MODULE)

run-ui-dev:
	npm run dev --prefix $(UI_MODULE)

build-db-migration-image:
	docker build --no-cache -t sdq-db-migration $(DB_MODULE)/.

build-db: clear-db build-db-migration-image
	docker compose -f $(DB_MODULE)/docker-compose.yaml up

clear-db:
	rm -rf $(DATABASE_FILE)

connect-db:
	sqlite3 $(DATABASE_FILE)