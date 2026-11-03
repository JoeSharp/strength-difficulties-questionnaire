# Developer Getting Started Guide

This guide will explain the tooling that has been used so far by the sole developer. Things like Code editors are obviously down to the individual developer, but it's good to know
which workflows have actually been tested.

## Operating System

This has been developed on a Mac, and also within WSL. So any Linux environment should work well.
I can make no garauntees about running it natively in Windows, just use WSL if you have Windows.

## Tools

The following tooling will be required:

- A code editor
  - [VS Code](https://code.visualstudio.com/) was used for everthing except for Java
  - [IntelliJ](https://www.jetbrains.com/idea/) was used for the Java analysis backend

- [Docker](https://www.docker.com/).
  Docker Compose is used for the application and development environments. The dev containers include Node, Java, Rust, and Go toolchains.

- [Just](https://github.com/casey/just)
  The `justfile` provides shortcuts for common development tasks.

## Running the application

Start the normal application stack with:

```bash
just run
```

This will invoke the builds of the various docker containers, and then run them in Docker compose. It should then be possible to visit the UI locally on

[Local SDQ](http://localhost:8080)

# Repository Layout

This monorepo contains everything required to get this system up and running.
It's under active development, and I've changed several key aspects already.
Currently the application is split into two functional verticals

- SDQ Analysis
- - The bulk ingest of spreadsheets
- - The running of queries across the whole dataset.
- SDQ Submission
- - An application for submitting individual data items, essentially replacing the spreadsheet based data capture.

Both aspects sit on top of the same database. The creation of that database is a distinct module.

### sdq-database-flyway

A container that uses Flyway to build the database from versioned SQL migrations.
The `DB_MODULE` variable in `.env` selects the migration project used by both Docker Compose files.

### sdq-analysis-api-java

The original backend service which contains the code for querying a postgres database.
The application is written using Spring Boot, exposing a REST API with the various queries.
This backend application also serves up the static resources of the frontend. This makes it easier to deploy.

### sdq-analysis-api-rust

A completely rewrite of the analysis API. Originally I used Java & Spring Boot since that's what I work with on client projects, then I noticed just how greedy the containers were.
The rust implementation takes a fraction (something like 2%) of the container size, and running memory footprint. So once this implementation is complete, the java one will be deprecated.

### sdq-analysis-ui

This is the front end for the analysis part of the system.
It uses Vite to build a React application.

### sdq-submission-api-go

This is a backend which will provide functionality for submitting new SDQ data in the first place. The current analysis system assumes data is captured via Excel Spreadsheets, then bulk ingested. In the long run, it would be much nicer to capture the data directly via a web interface.

## local

This contains the docker compose files required to run the system up. At the moment, docker compose is the only mechanism I've ever used to run the whole stack.

Potentially we could start messing with Kubernetes, but that's overkill at this stage, and I haven't ever tried building up a database in K8s so would recommend using AWS RDS for that.

## Development containers

The dev workflows in `local/docker-compose.dev.yaml` extend the shared database services in `local/docker-compose.yaml`. They mount source code into containers and restart backend processes when files change; Vite serves the UIs with hot reload. The first run builds the dev images and downloads dependencies; no language toolchains need to be installed on the host. Each workflow starts the database and applies migrations as needed.

| Workflow | Command | URL |
| --- | --- | --- |
| Java analysis API and UI | `just run-analysis-api-java-with-ui` | http://localhost:5173 |
| Java analysis API only | `just run-analysis-api-java` | http://localhost:8080 |
| Rust analysis API and UI | `just run-dev-rust` | http://localhost:5175 |
| Rust analysis API only | `just run-analysis-api-rust` | http://localhost:8081 |
| Go submission API and UI | `just run-submission-api-build-ui` | http://localhost:5174 |
| Go submission API only | `just run-submission-api` | http://localhost:8082 |
| All dev services | `just run-dev` | See URLs above |

### Develop the Submission UI

Start the submission UI and its Go API with:

```bash
just run-submission-ui
```

Edit the UI or API source and the running dev services will reload.

The UIs can also be started on their own; their API service starts automatically. Press Ctrl+C to stop the selected dev stack. Run `just stop` to stop the application and test stacks.
