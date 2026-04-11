package uk.ratracejoe.sdq.repository;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.model.gbo.GboSubmission;

@RequiredArgsConstructor
public class GboRepositoryImpl implements GboRepository {
  private final JdbcClient jdbcClient;

  private static final String TABLE_NAME = "gbo_score";

  private static final String FIELD_GOAL_ID = "goal_id";
  private static final String FIELD_ASSESSOR = "assessor";
  private static final String FIELD_PERIOD_DATE = "period_date";
  private static final String FIELD_SCORE = "score";

  public void save(GboSubmission domain) {
    String sql =
        String.format(
            "INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
            TABLE_NAME, FIELD_GOAL_ID, FIELD_PERIOD_DATE, FIELD_ASSESSOR, FIELD_SCORE);

    AtomicInteger paramIndex = new AtomicInteger(1);
    jdbcClient
        .sql(sql)
        .param(paramIndex.getAndIncrement(), domain.goalId())
        .param(paramIndex.getAndIncrement(), domain.period())
        .param(paramIndex.getAndIncrement(), domain.assessor().name())
        .param(paramIndex.getAndIncrement(), domain.score())
        .update();
  }

  @Override
  public int deleteAll() {
    return jdbcClient.sql(String.format("DELETE FROM %s", TABLE_NAME)).update();
  }
}
