DB_MODULE=sdq-database
APP_MODULE=spring-app
UI_MODULE=sdq-ui

# Required to provide a consistent IP address that can be used inside containers
# to reach things outside containers, and vice versa
export LOCAL_STACK_CIDR=172.16.10.0/24
export LOCAL_STACK_HOST=172.16.10.1
export APPLICATION_NAME=sdq

# Defined here so they can be used in Docker
# and for constructing YAML files for K8s
export SDQ_DATABASE_NAME=sdq
export SDQ_DATABASE_USERNAME=sdqUser
export SDQ_DATABASE_PASSWORD=GYUsdfsd234
export SDQ_DATABASE_URL=jdbc:postgresql://localhost:5432/$(SDQ_DATABASE_NAME)
export SDQ_CLIENT_ID=timesheets-service
export SDQ_CLIENT_SECRET=rX0uyWb89PxdeclkQoLMtmRtCLRxFlKy

export KEYCLOAK_DATABASE_NAME=kc
export KEYCLOAK_DATABASE_USERNAME=keycloak
export KEYCLOAK_DATABASE_PASSWORD=kc-password
export KEYCLOAK_DATABASE_ROOT_PASSWORD=superSecret
export KEYCLOAK_ADMIN_USERNAME=keycloak
export KEYCLOAK_ADMIN_PASSWORD=Q@v3rTYT
export KEYCLOAK_ISSUER_URI=http://localhost:8085/realms/ratracejoe

run: copy-ui run-service

build-jar: copy-ui
	./gradlew :spring-app:bootJar

export DATABASE_URL=$(SDQ_DATABASE_URL)
export DATABASE_USERNAME=$(SDQ_DATABASE_USERNAME)
export DATABASE_PASSWORD=$(SDQ_DATABASE_PASSWORD)
export OAUTH2_ISSUER_URI=$(KEYCLOAK_ISSUER_URI)
export CLIENT_ID=$(SDQ_CLIENT_ID)
export CLIENT_SECRET=$(SDQ_CLIENT_SECRET)

run-service-dev:
	./gradlew :spring-app:bootRun

run-service-dev-ui: copy-ui run-service-dev

clean-run: build-db run

copy-ui: build-ui
	rm -rf $(APP_MODULE)/src/main/resources/static && cp -R sdq-ui/dist $(APP_MODULE)/src/main/resources/static

build-ui:
	npm run build --prefix $(UI_MODULE)

run-ui-dev:
	npm run dev --prefix $(UI_MODULE)

docker-run-db:
	docker compose -f local/docker-compose.yaml up sdq-db

docker-run-app: build-app-image build-db-migration-image
	docker compose -f local/docker-compose.yaml up

docker-run-deps: build-db-migration-image
	docker compose -f local/docker-compose.yaml up sdq-db-migration auth

docker-stop-app:
	docker compose -f local/docker-compose.yaml down

build-app-image:
	docker build -t sdq-app .

build-db-migration-image:
	docker build -t sdq-db-migration $(DB_MODULE)/.

build-db: build-db-migration-image
	docker compose -f $(DB_MODULE)/docker-compose.yaml up

# Useful commands to connect to the various dependencies for manual interaction
connect-db:
	echo "Connecting to database"
	docker exec -it ${APPLICATION_NAME}-db psql -d ${SDQ_DATABASE_NAME} -U ${SDQ_DATABASE_USERNAME}
