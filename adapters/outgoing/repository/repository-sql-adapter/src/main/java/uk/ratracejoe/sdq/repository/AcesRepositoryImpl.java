package uk.ratracejoe.sdq.repository;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.model.demographics.AceType;

@RequiredArgsConstructor
public class AcesRepositoryImpl implements AcesRepository {
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

  @Override
  public Map<AceType, Integer> getForClient(UUID clientId) {
    return jdbcClient
        .sql("SELECT ace_type, score FROM aces WHERE client_id = :clientId")
        .param("clientId", clientId)
        .query(
            (rs, rowNum) ->
                Map.entry(AceType.valueOf(rs.getString("ace_type")), rs.getInt("score")))
        .stream()
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @Override
  public int deleteAll() {
    return jdbcClient.sql("DELETE FROM aces").update();
  }

  @Override
  public int deleteForClient(UUID clientId) {
    return jdbcClient.sql("DELETE FROM aces WHERE client_id = ?").param(1, clientId).update();
  }
}
