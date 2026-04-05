package uk.ratracejoe.sdq.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.entity.InterventionTypeEntity;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.demographics.InterventionType;

@RequiredArgsConstructor
public class InterventionTypeRepositoryImpl implements InterventionTypeRepository {
  private final JdbcClient jdbcClient;

  private static final String TABLE_NAME = "intervention_type";
  private static final String FIELD_CLIENT_ID = "client_id";
  private static final String FIELD_INTERVENTION_TYPE = "intervention_type";

  public void save(UUID fileId, InterventionType interventionType) {
    jdbcClient.sql(insertOptionSQL()).param(1, fileId).param(2, interventionType.name()).update();
  }

  @Override
  public int deleteAll() {
    return jdbcClient.sql(deleteAllSQL()).update();
  }

  public List<InterventionTypeEntity> getByFile(UUID fileId) throws SdqException {
    return jdbcClient
        .sql(getByFileSQL())
        .param(1, fileId)
        .query(
            (rs, rowNum) -> {
              InterventionType interventionType =
                  Optional.ofNullable(rs.getString(FIELD_INTERVENTION_TYPE))
                      .map(InterventionType::valueOf)
                      .orElseGet(InterventionType::defaultValue);
              return new InterventionTypeEntity(fileId, interventionType);
            })
        .list();
  }

  static String getByFileSQL() {
    return String.format(
        "SELECT %s FROM %s WHERE %s = ?", FIELD_INTERVENTION_TYPE, TABLE_NAME, FIELD_CLIENT_ID);
  }

  static String insertOptionSQL() {
    return String.format(
        "INSERT INTO %s (%s, %s) VALUES (? ,?) ON CONFLICT DO NOTHING",
        TABLE_NAME, FIELD_CLIENT_ID, FIELD_INTERVENTION_TYPE);
  }

  static String deleteAllSQL() {
    return String.format("DELETE FROM %s", TABLE_NAME);
  }
}
