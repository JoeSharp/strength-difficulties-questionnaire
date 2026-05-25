package uk.ratracejoe.sdq.repository;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.model.demographics.Intervention;

@RequiredArgsConstructor
public class InterventionRepositoryImpl {
  private final JdbcClient jdbcClient;

  public void save(UUID clientId, Intervention interventionType) {
    jdbcClient
        .sql(
            """
        INSERT INTO
        intervention_type
        (client_id, intervention_type, sessions)
         VALUES (:clientId ,:interventionType, :sessions) ON CONFLICT DO NOTHING
         """)
        .param("clientId", clientId)
        .param("interventionType", interventionType.type().name())
        .param("sessions", interventionType.sessions())
        .update();
  }

  public int deleteForClient(UUID clientId) {
    return jdbcClient
        .sql("DELETE FROM intervention_type WHERE client_id = :clientId")
        .param("clientId", clientId)
        .update();
  }
}
