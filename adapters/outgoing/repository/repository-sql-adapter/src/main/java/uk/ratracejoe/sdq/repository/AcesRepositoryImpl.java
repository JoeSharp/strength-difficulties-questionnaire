package uk.ratracejoe.sdq.repository;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.model.demographics.AceType;

@RequiredArgsConstructor
public class AcesRepositoryImpl {
  private final JdbcClient jdbcClient;

  public void save(UUID clientId, AceType aceType, Integer score) {
    jdbcClient
        .sql(
            """
        INSERT INTO
        aces
        (client_id, ace_type, score)
         VALUES (:clientId ,:aceType, :score) ON CONFLICT DO NOTHING
         """)
        .param("clientId", clientId)
        .param("aceType", aceType.name())
        .param("score", score)
        .update();
  }

  public int deleteForClient(UUID clientId) {
    return jdbcClient
        .sql("DELETE FROM aces WHERE client_id = :clientId")
        .param("clientId", clientId)
        .update();
  }
}
