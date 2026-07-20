set dotenv-load := true

DB_MODULE := env_var("DB_MODULE")
API_MODULE_JAVA := env_var("API_MODULE_JAVA")
API_MODULE_RUST := env_var("API_MODULE_RUST")
UI_MODULE := env_var("UI_MODULE")
APPLICATION_NAME := env_var("APPLICATION_NAME")
SDQ_DATABASE_NAME := env_var("SDQ_DATABASE_NAME")
SDQ_DATABASE_USERNAME := env_var("SDQ_DATABASE_USERNAME")

# Default task
default: docker-run-app

# Run the whole application locally in Docker
start: docker-run-app

# Stop the application
stop: docker-stop-all

# Install UI dependencies
install-ui:
    npm install --prefix {{UI_MODULE}}

# Build the user interface
build-ui: install-ui
    npm run build --prefix {{UI_MODULE}}

# Copy the static resources of the UI into the public backend folder.
copy-ui: build-ui
    rm -rf {{API_MODULE_JAVA}}/src/main/resources/static
    cp -R sdq-ui/dist {{API_MODULE_JAVA}}/src/main/resources/static

run-service-dev-rust: 
    cargo run --manifest-path sdq-api-rust/app/Cargo.toml

run-dev-rust: copy-ui run-service-dev-rust

build-api-rust:
    cargo build --manifest-path sdq-api-rust/app/Cargo.toml

# Run the service via gradle
run-service-dev-java:
    ./gradlew :spring-app:bootRun

# Build the UI and Run the service via gradle
run-dev-java: copy-ui run-service-dev-java

# Build the JAR file
build-api-java:
    ./gradlew :spring-app:bootJar

# Build the UI and bundle into application JAR file
build-java: copy-ui build-api-java

# Runs the user interface in hot reloading mode.
run-ui-dev: install-ui
    npm run dev --prefix {{UI_MODULE}}

# Run the backend unit tests
test-service-java:
    ./gradlew test --info

test-service-rust:
    cargo test --manifest-path sdq-api-rust/app/Cargo.toml

# Run the dependencies required by unit tests
# Always clean them out first
docker-run-test-deps:
    docker compose -f local/docker-compose.test.yaml down --volumes
    docker compose -f local/docker-compose.test.yaml up --build --wait

# Run the unit tests, which depends on us running containers
run-tests: docker-run-test-deps test-service-java

# Run the app images as they are
docker-run-app-no-build:
    docker compose -f local/docker-compose.yaml --profile api-rust up -d --wait

# Run the entire system up within Docker
docker-run-app:
    docker compose -f local/docker-compose.yaml --profile api-rust --profile api-java up --build -d --wait

# Run the app dependencies in docker, but not the app itself
# Use run-service-dev-java for that
docker-run-deps:
    docker compose -f local/docker-compose.yaml up --build -d --wait

# Stop the application stack
docker-stop:
    docker compose -f local/docker-compose.yaml --profile api-java --profile api-rust down

# Stop the test dependencies
docker-stop-test:
    docker compose -f local/docker-compose.test.yaml --profile api-java --profile api-rust down

# Stop any docker containers relating to this application
docker-stop-all: docker-stop docker-stop-test

docker-build-rust-api:
    docker build -t sdq-api-rust -f Dockerfile.rust .

# Build the Docker image for the application
docker-build-java-api:
    docker build -t sdq-app -f Dockerfile.java .

# Build the Docker image for the database migration
docker-build-db-migration:
    docker build -t sdq-db-migration {{DB_MODULE}}/.

# Run the migration on its own
docker-db-migrate:
    docker compose -f local/docker-compose.yaml sdq-db-migration

# take down the docker stack, but also remove volumes
docker-clean:
    docker compose -f local/docker-compose.yaml down --volumes
    docker compose -f local/docker-compose.test.yaml down --volumes

# Connect a shell to the database.
connect-db:
    echo "Connecting to database"
    docker exec -it {{APPLICATION_NAME}}-db psql -d {{SDQ_DATABASE_NAME}} -U {{SDQ_DATABASE_USERNAME}}

connect-test-db:
    echo "Connecting to test database"
    docker exec -it {{APPLICATION_NAME}}-test-db psql -d {{SDQ_DATABASE_NAME}} -U test

