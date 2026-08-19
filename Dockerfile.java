# Stage 1: Build UI
FROM node:24-alpine AS ui-builder
ARG ANALYSIS_UI_MODULE=sdq-analysis-ui
WORKDIR /ui

# Copy only package files first for caching
COPY ${ANALYSIS_UI_MODULE}/package.json sdq-analysis-ui/package-lock.json ./
RUN npm install

# Copy the rest of the UI source
COPY sdq-analysis-ui/ .

# Build the UI (produces /ui/dist)
RUN npm run build

# Stage: Build Backend
FROM eclipse-temurin:21-jdk AS backend-builder
ARG ANALYSIS_API_MODULE_JAVA=sdq-analysis-api-java
ARG APP_MODULE=spring-app
WORKDIR /app

# Copy and build just gradle itself for caching
COPY ${ANALYSIS_API_MODULE_JAVA}/gradlew ${ANALYSIS_API_MODULE_JAVA}/gradlew.bat ./
COPY ${ANALYSIS_API_MODULE_JAVA}/gradle ./gradle
COPY ${ANALYSIS_API_MODULE_JAVA}/build.gradle ${ANALYSIS_API_MODULE_JAVA}/settings.gradle ./
RUN ./gradlew dependencies

# Copy the backend source
COPY ${ANALYSIS_API_MODULE_JAVA} .

# Copy UI build output into Spring Boot static resources
RUN rm -rf ${APP_MODULE}/src/main/resources/static
COPY --from=ui-builder /ui/dist ${APP_MODULE}/src/main/resources/static
COPY ${ANALYSIS_API_MODULE_JAVA}/entrypoint.sh /entrypoint.sh

RUN ./gradlew build :${APP_MODULE}:bootJar --no-daemon -x test
RUN test -f /app/${APP_MODULE}/build/libs/${APP_MODULE}-0.0.1-SNAPSHOT.jar

# Stage: Runtime
FROM amazoncorretto:21-alpine-jdk
ARG APP_MODULE=spring-app
LABEL authors="ratracejoe.co.uk"

# Install curl
RUN apk --no-cache add curl

COPY --from=backend-builder /app/${APP_MODULE}/build/libs/${APP_MODULE}-0.0.1-SNAPSHOT.jar app.jar

HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 CMD curl -f http://localhost:8080/api/actuator/health || exit 1

COPY --from=backend-builder /entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
# Set the entrypoint
ENTRYPOINT ["/entrypoint.sh"]
