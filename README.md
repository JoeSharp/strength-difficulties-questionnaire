# Strengths Difficulties Questionnaire Analysis

This system allows multiple SDQ spreadsheets to be ingested into a single data store.
It then becomes possible to run sophisticated queries across the entire data set.

## Repository Layout

This monorepo contains everything required to get this system up and running.
It's under active development, and I've changed several key aspects already.
Currently the application is split into two functional verticals

- SDQ Analysis
- - The bulk ingest of spreadsheets
- - The running of queries across the whole dataset.
- SDQ Submission
- - An application for submitting individual data items, essentially replacing the spreadsheet based data capture.

Both aspects sit on top of the same database. The creation of that database is a distinct module.

### sdq-database-liquibase

A container that uses liquibase to build the database.
I have already decided liquibase is too greedy (1GB image!) so will migrate to Flyway.

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

## Client requirements...

brief from email

Questions they will need to interrogate their whole database of eligible cases will include things like:

- In the 2024/25 school year how many girls had an emotional SDQ score over 7?
- In the 2024/25 school year how many boys made 4 or more points progress in GBO's?
- In the 2024/25 school year how many looked after children had a goal-based outcome categorised as trauma recovery?
- In the 2024/25 school year how many children had an increased SDQ and/or GBO score that were funded by SGO?
