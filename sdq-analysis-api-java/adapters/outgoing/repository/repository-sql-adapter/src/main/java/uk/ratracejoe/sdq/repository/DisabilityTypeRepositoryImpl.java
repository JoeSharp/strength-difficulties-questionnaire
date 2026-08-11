package uk.ratracejoe.sdq.repository;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.model.demographics.DisabilityType;

@RequiredArgsConstructor
public class DisabilityTypeRepositoryImpl {
  private final JdbcClient jdbcClient;

  public void save(UUID clientId, DisabilityType disabilityType) {
    jdbcClient
        .sql(
            """
        INSERT INTO
        disability_type
        (client_id, disability_type)
         VALUES (:clientId, :disabilityType) ON CONFLICT DO NOTHING
         """)
        .param("clientId", clientId)
        .param("disabilityType", disabilityType.name())
        .update();
  }

  public int deleteForClient(UUID clientId) {
    return jdbcClient
        .sql("DELETE FROM disability_type WHERE client_id = :clientId")
        .param("clientId", clientId)
        .update();
  }
}
