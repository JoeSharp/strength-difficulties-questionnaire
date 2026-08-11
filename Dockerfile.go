# syntax=docker/dockerfile:1

# Build the application from source
FROM golang:1.26.3 AS build-stage

WORKDIR /src

COPY sdq-api-go/ .
RUN go work sync

RUN cd app && CGO_ENABLED=0 GOOS=linux go build -o /sdq-api-go

# Run the tests in the container
#FROM build-stage AS run-test-stage
#RUN go test -v ./...

# Deploy the application binary into a lean image
FROM gcr.io/distroless/base-debian11 AS build-release-stage

WORKDIR /

COPY --from=build-stage /sdq-api-go /sdq-api-go

EXPOSE 8080

USER nonroot:nonroot

ENTRYPOINT ["/sdq-api-go"]