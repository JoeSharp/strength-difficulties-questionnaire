run: copy-ui run-service

run-service:
	./gradlew bootRun

copy-ui: build-ui
	cp -R sdq-ui/dist/* src/main/resources/static

build-ui:
	npm run build --prefix ./sdq-ui/