FROM liquibase/liquibase:alpine

COPY ./liquibase/changelog /liquibase/changelog
COPY ./liquibase.properties /liquibase/liquibase.properties

ENTRYPOINT ["liquibase", "--defaultsFile=/liquibase/liquibase.properties", "update"]