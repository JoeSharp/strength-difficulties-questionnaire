# Strengths Difficulties Questionnaire

## What SDQ is

The Strengths and Difficulties Questionnaire (SDQ) is a brief behavioural screening tool for children and young people.

It is commonly completed by parents, carers, teachers, or young people themselves, and helps services understand emotional and behavioural wellbeing across areas such as emotional symptoms, conduct, hyperactivity, peer relationships, and prosocial behaviour.

In this project, SDQ data is used as one of the key outcome measures to track change over time and support service evaluation.

## Where this app came from

This project started from a personal conversation.

A friend of mine, who volunteered at the Every Cloud charity, built a spreadsheet to track outcomes for children and young people. Each young person had their own copy of the spreadsheet.

Having the data in different spreadsheets made questions like this hard to answer:

- How many children improved this school year?
- Are outcomes different by gender, age, or referral type?
- Which funding streams are linked to better progress?

Questions I was explicitly given during the early development:

- In the 2024/25 school year how many girls had an emotional SDQ score over 7?
- In the 2024/25 school year how many boys made 4 or more points progress in GBO's?
- In the 2024/25 school year how many looked after children had a goal-based outcome categorised as trauma recovery?
- In the 2024/25 school year how many children had an increased SDQ and/or GBO score that were funded by SGO?

I looked at the spreadsheet and suggested ingesting all the data into a single database would allow us to query across it.

## Why this matters

The end goal is better decision-making.

When outcome data is easier to access and trust, teams can:

- Understand whether support is helping
- Spot patterns earlier
- Report impact to stakeholders with confidence
- Plan services based on evidence, not guesswork

## Why this is a good project for work experience students

This codebase is useful for students because it combines technical and real-world thinking.

You can learn how software supports a real service problem, not just a coding exercise.

Students can explore:

- **Data engineering basics**: parsing files, validation, and transformation
- **API design**: exposing useful and safe query endpoints
- **Frontend analysis UX**: making complex data understandable
- **Testing**: checking that imported data and calculations are correct
- **Multi-language architecture**: understanding why different services are built in Java, Rust, Go, and TypeScript

## Project Evolution

### First Version - Analysis only

The first version of this application has a 'bulk upload' user interface.

- Users would take a collection of spreadsheets
- Upload them all into the system
- Run their queries
- Potentially clear down the system

The data still 'lives' in the spreadsheets, and the SDQ Analysis application just provides a means of querying across them.

This is the current state of the application.

### Potential Future Version - Capture and Analysis

With the database in place, it should be possible to build a web based system to capture the data directly into the database. This would change the system from a 'temporary analysis store' to the authoratative record of the questionnaire results.

In this version of the system, the workflow would be

- The system is running long term
- Charity volunteers would register new 'clients' (generally young people) in the system
  - capturing their demographic information
  - capturing specific goals
  - outlining a schedule for recording SDQ
- At given intervals, the volunteer associated with a given client would
  - Trigger a request for SDQ response from
    - School
    - Parent/Carer 1
    - Parent/Carer 2
    - The child themselves
- These individuals would then record their responses
  - These would be directly written to the database

This greatly increases the importance of this system to the workflow because now

- All key players would be interacting directly with it
- The data would 'live' in the database and it would need backing up
- Different users would require different levels of access
  - Role based security
  - Auditing and Logging will be important new features
