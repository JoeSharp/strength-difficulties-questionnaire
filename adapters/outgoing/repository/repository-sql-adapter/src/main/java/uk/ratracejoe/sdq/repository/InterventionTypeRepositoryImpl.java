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

  public void save(UUID clientId, InterventionType interventionType) {
    jdbcClient
        .sql(
            "INSERT INTO intervention_type (client_id, intervention_type) VALUES (? ,?) ON CONFLICT DO NOTHING")
        .param(1, clientId)
        .param(2, interventionType.name())
        .update();
  }

  @Override
  public List<InterventionType> getForClient(UUID clientId) {
    return jdbcClient
        .sql("SELECT intervention_type FROM intervention_type WHERE client_id = ?")
        .param(1, clientId)
        .query(InterventionType.class)
        .list();
  }

  @Override
  public int deleteAll() {
    return jdbcClient.sql("DELETE FROM intervention_type").update();
  }

  @Override
  public int deleteForClient(UUID clientId) {
    return jdbcClient
        .sql("DELETE FROM intervention_type WHERE client_id = ?")
        .param(1, clientId)
        .update();
  }

  public List<InterventionTypeEntity> getByFile(UUID clientId) throws SdqException {
    return jdbcClient
        .sql("SELECT intervention_type FROM intervention_type WHERE client_id = ?")
        .param(1, clientId)
        .query(
            (rs, rowNum) -> {
              InterventionType interventionType =
                  Optional.ofNullable(rs.getString("intervention_type"))
                      .map(InterventionType::valueOf)
                      .orElseGet(InterventionType::defaultValue);
              return new InterventionTypeEntity(clientId, interventionType);
            })
        .list();
  }
}
