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

  public void save(UUID clientId, InterventionType interventionType) {
    jdbcClient
        .sql(
            String.format(
                "INSERT INTO %s (%s, %s) VALUES (? ,?) ON CONFLICT DO NOTHING",
                TABLE_NAME, FIELD_CLIENT_ID, FIELD_INTERVENTION_TYPE))
        .param(1, clientId)
        .param(2, interventionType.name())
        .update();
  }

  @Override
  public List<InterventionType> getForClient(UUID clientId) {
    return jdbcClient
        .sql(
            String.format(
                "SELECT %s FROM %s WHERE %s = ?",
                FIELD_INTERVENTION_TYPE, TABLE_NAME, FIELD_CLIENT_ID))
        .param(1, clientId)
        .query(InterventionType.class)
        .list();
  }

  @Override
  public int deleteAll() {
    return jdbcClient.sql(String.format("DELETE FROM %s", TABLE_NAME)).update();
  }

  @Override
  public int deleteForClient(UUID clientId) {
    return jdbcClient
        .sql(String.format("DELETE FROM %s WHERE %s = ?", TABLE_NAME, FIELD_CLIENT_ID))
        .param(1, clientId)
        .update();
  }

  public List<InterventionTypeEntity> getByFile(UUID clientId) throws SdqException {
    return jdbcClient
        .sql(
            String.format(
                "SELECT %s FROM %s WHERE %s = ?",
                FIELD_INTERVENTION_TYPE, TABLE_NAME, FIELD_CLIENT_ID))
        .param(1, clientId)
        .query(
            (rs, rowNum) -> {
              InterventionType interventionType =
                  Optional.ofNullable(rs.getString(FIELD_INTERVENTION_TYPE))
                      .map(InterventionType::valueOf)
                      .orElseGet(InterventionType::defaultValue);
              return new InterventionTypeEntity(clientId, interventionType);
            })
        .list();
  }
}
