# Stage 1: Build UI
FROM node:24-alpine AS ui-builder
ARG UI_MODULE=sdq-ui
WORKDIR /ui

# Copy only package files first for caching
COPY ${UI_MODULE}/package.json sdq-ui/package-lock.json ./
RUN npm install

# Copy the rest of the UI source
COPY sdq-ui/ .

# Build the UI (produces /ui/dist)
RUN npm run build

# Stage: Build Backend
FROM eclipse-temurin:21-jdk AS backend-builder
ARG APP_MODULE=spring-app
WORKDIR /app

 # Copy and build just gradle itself for caching
COPY gradlew gradlew.bat ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies

# Copy the backend source
COPY . .

# Copy UI build output into Spring Boot static resources
RUN rm -rf ${APP_MODULE}/src/main/resources/static
COPY --from=ui-builder /ui/dist ${APP_MODULE}/src/main/resources/static

RUN ./gradlew build :spring-app:bootJar --no-daemon -x test
RUN test -f /app/spring-app/build/libs/spring-app-0.0.1-SNAPSHOT.jar

# Stage: Runtime
FROM amazoncorretto:21-alpine-jdk
LABEL authors="joesharpcs.co.uk"

# Install curl
RUN apk --no-cache add curl

COPY --from=backend-builder /app/spring-app/build/libs/spring-app-0.0.1-SNAPSHOT.jar app.jar

HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 CMD curl -f http://localhost:8080/api/actuator/health || exit 1

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
# Set the entrypoint
ENTRYPOINT ["/entrypoint.sh"]
