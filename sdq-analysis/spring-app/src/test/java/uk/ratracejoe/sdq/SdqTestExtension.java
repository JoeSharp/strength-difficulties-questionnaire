package uk.ratracejoe.sdq;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ratracejoe.sdq.config.DbConfig;

public class SdqTestExtension implements BeforeEachCallback, AfterEachCallback {
  private static final Logger LOGGER = LoggerFactory.getLogger(SdqTestExtension.class);

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    createDatabase(context);
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    deleteDatabase(context);
  }

  public static void createDatabase(ExtensionContext context) throws SQLException {
    ApplicationContext appContext = SpringExtension.getApplicationContext(context);
    DbConfig dbConfig = appContext.getBean(DbConfig.class);
    try {
      DataSource dataSource = dbConfig.dataSource();
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

  private void deleteDatabase(ExtensionContext context) {
    ApplicationContext appContext = SpringExtension.getApplicationContext(context);
    DbConfig dbConfig = appContext.getBean(DbConfig.class);
    LOGGER.info("Ending SDQ Test");
    try {
      Files.delete(Path.of(dbConfig.getDatabaseFile()));
      LOGGER.info("Deleted existing test database {}", dbConfig.getDatabaseFile());
    } catch (Exception e) {
      LOGGER.info("Couldn't delete database, but that may be fine");
    }
  }
}
