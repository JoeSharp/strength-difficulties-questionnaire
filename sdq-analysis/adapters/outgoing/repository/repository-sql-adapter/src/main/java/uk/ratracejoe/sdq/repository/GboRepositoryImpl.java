package uk.ratracejoe.sdq.repository;

import java.sql.Date;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.model.gbo.GboSubmission;

@RequiredArgsConstructor
public class GboRepositoryImpl implements GboRepository {
  private final JdbcClient jdbcClient;

  private static final String TABLE_NAME = "gbo";

  private static final String FIELD_CLIENT_ID = "client_id";
  private static final String FIELD_ASSESSOR = "assessor";
  private static final String FIELD_PERIOD_DATE = "period_date";
  private static final String FIELD_SCORE_INDEX = "score_index";
  private static final String FIELD_SCORE = "score";

  public void save(GboSubmission domain) {
    String sql = insertSQL();

    domain
        .scores()
        .forEach(
            score ->
                jdbcClient
                    .sql(sql)
                    .param(1, domain.clientId())
                    .param(
                        2,
                        Date.valueOf(domain.period().atZone(ZoneId.systemDefault()).toLocalDate()))
                    .param(3, domain.assessor().name())
                    .param(4, score.scoreIndex())
                    .param(5, score.score())
                    .update());
  }

  @Override
  public int deleteAll() {
    return jdbcClient.sql(deleteAllSQL()).update();
  }

  static String insertSQL() {
    return String.format(
        "INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?)",
        TABLE_NAME,
        FIELD_CLIENT_ID,
        FIELD_PERIOD_DATE,
        FIELD_ASSESSOR,
        FIELD_SCORE_INDEX,
        FIELD_SCORE);
  }

  static String deleteAllSQL() {
    return String.format("DELETE FROM %s", TABLE_NAME);
  }
}
