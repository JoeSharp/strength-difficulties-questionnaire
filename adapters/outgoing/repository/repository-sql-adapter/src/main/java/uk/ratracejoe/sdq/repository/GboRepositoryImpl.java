package uk.ratracejoe.sdq.repository;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.model.gbo.GboSubmission;

@RequiredArgsConstructor
public class GboRepositoryImpl implements GboRepository {
  private final JdbcClient jdbcClient;

  public static final String FIELD_GOAL_ID = "goal_id";
  public static final String FIELD_ASSESSOR = "assessor";
  public static final String FIELD_PERIOD_DATE = "period_date";
  public static final String FIELD_SCORE = "score";

  public void save(GboSubmission domain) {
    AtomicInteger paramIndex = new AtomicInteger(1);
    jdbcClient
        .sql("INSERT INTO gbo_score (goal_id, period_date, assessor, score) VALUES (?, ?, ?, ?)")
        .param(paramIndex.getAndIncrement(), domain.goalId())
        .param(paramIndex.getAndIncrement(), domain.period())
        .param(paramIndex.getAndIncrement(), domain.assessor().name())
        .param(paramIndex.getAndIncrement(), domain.score())
        .update();
  }

  @Override
  public int deleteAll() {
    return jdbcClient.sql("DELETE FROM gbo_score").update();
  }
}
