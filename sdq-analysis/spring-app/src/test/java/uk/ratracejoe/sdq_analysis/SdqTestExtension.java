package uk.ratracejoe.sdq_analysis;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ratracejoe.sdq_analysis.config.DbConfig;

public class SdqTestExtension implements BeforeEachCallback, AfterEachCallback {
  private static final Logger LOGGER = LoggerFactory.getLogger(SdqTestExtension.class);

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    deleteDatabase(context);
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    deleteDatabase(context);
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
