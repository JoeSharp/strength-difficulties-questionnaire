package uk.ratracejoe.sdq;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SdqDatabaseInitializer {
  private static final Logger LOGGER = LoggerFactory.getLogger(SdqDatabaseInitializer.class);

  public void resetAndMigrate(DataSource dataSource) {
    try {
      resetDatabase(dataSource);
      createDatabase(dataSource);
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  private void resetDatabase(DataSource dataSource) throws Exception {
    try (Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement()) {

      stmt.execute("DROP SCHEMA public CASCADE");
      stmt.execute("CREATE SCHEMA public");
    }
  }

  private void createDatabase(DataSource dataSource) throws SQLException {
    try {
      Connection connection = dataSource.getConnection();
      Database database =
          DatabaseFactory.getInstance()
              .findCorrectDatabaseImplementation(new JdbcConnection(connection));
      Liquibase liquibase =
          new Liquibase(
              "changelog/db.changelog-master.yaml",
              new DirectoryResourceAccessor(new File("../sdq-database/liquibase/")),
              database);

      liquibase.update(new Contexts(), new LabelExpression());

    } catch (Exception e) {
      LOGGER.error("Failed to run Liquibase migration", e);
      throw new SQLException(e);
    }
  }
}
