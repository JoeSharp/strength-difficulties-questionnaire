package uk.ratracejoe.sdq.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.ratracejoe.sdq.config.DbConfig;
import uk.ratracejoe.sdq.database.tables.*;
import uk.ratracejoe.sdq.model.DemographicField;
import uk.ratracejoe.sdq.model.SdqEnumerations;
import uk.ratracejoe.sdq.repository.DatabaseService;

@Component
@RequiredArgsConstructor
public class DatabaseServiceImpl implements DatabaseService {
  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseServiceImpl.class);

  private final DataSource dataSource;
  private final DbConfig dbConfig;

  @Override
  public boolean databaseExists() {
    return Files.exists(Path.of(dbConfig.getDatabaseFile()));
  }

  @Override
  public void ensureEnumerations(SdqEnumerations sdqEnumerations) {
    try (Connection conn = dataSource.getConnection()) {
      if (conn != null) {
        LOGGER.info("Connected to SQLite.");
        createDemographicOptions(conn, sdqEnumerations.demographics());
        LOGGER.info("Database created for SDQ");
      }
    } catch (SQLException e) {
      LOGGER.error(e.getMessage());
    }
  }

  private void createDemographicOptions(
      Connection connection, Map<DemographicField, List<String>> demographics) {
    try (PreparedStatement stmt =
        connection.prepareStatement(DemographicOptionTable.insertOptionSQL())) {
      demographics.forEach(
          (key, value) ->
              value.forEach(
                  optionValue -> {
                    try {
                      stmt.setString(1, key.name());
                      stmt.setString(2, optionValue);
                      stmt.addBatch();
                    } catch (SQLException e) {
                      LOGGER.error(e.getMessage());
                    }
                  }));
      stmt.executeBatch();
    } catch (SQLException e) {
      LOGGER.error(e.getMessage());
    }
  }
}
