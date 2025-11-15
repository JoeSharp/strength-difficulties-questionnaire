package uk.ratracejoe.sdq.repository;

import static uk.ratracejoe.sdq.repository.RepositoryUtils.handle;
import static uk.ratracejoe.sdq.tables.DemographicOptionTable.FIELD_DEMOGRAPHIC;
import static uk.ratracejoe.sdq.tables.DemographicOptionTable.FIELD_OPTION_TEXT;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ratracejoe.sdq.entity.DemographicOptionEntity;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.DemographicField;
import uk.ratracejoe.sdq.model.SdqEnumerations;
import uk.ratracejoe.sdq.tables.DemographicOptionTable;

@RequiredArgsConstructor
public class DemographicOptionRepositoryImpl implements DemographicOptionRepository {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(DemographicOptionRepositoryImpl.class);
  private final DataSource dataSource;

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

  public Map<DemographicField, List<String>> getOptionsByField() throws SdqException {
    return getAll().stream()
        .collect(
            Collectors.toMap(
                DemographicOptionEntity::field,
                k -> List.of(k.optionText()),
                (a, b) -> Stream.concat(a.stream(), b.stream()).toList()));
  }

  public List<DemographicOptionEntity> getAll() throws SdqException {
    return handle(
        dataSource,
        "getAllDemographicOptions",
        DemographicOptionTable.getAll(),
        stmt -> {
          List<DemographicOptionEntity> results = new ArrayList<>();
          ResultSet rs = stmt.executeQuery();

          while (rs.next()) {
            DemographicField field = DemographicField.valueOf(rs.getString(FIELD_DEMOGRAPHIC));
            String optionText = rs.getString(FIELD_OPTION_TEXT);
            DemographicOptionEntity entity = new DemographicOptionEntity(field, optionText);
            results.add(entity);
          }

          return results;
        });
  }
}
