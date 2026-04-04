package uk.ratracejoe.sdq.repository;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.*;

@RequiredArgsConstructor
public class SdqRepositoryImpl implements SdqRepository {
  private final JdbcClient jdbcClient;

  private static final String TABLE_NAME = "sdq";
  private static final String FIELD_PERIOD_ID = "period_id";
  private static final String FIELD_ASSESSOR = "assessor";
  private static final String FIELD_STATEMENT = "statement";
  private static final String FIELD_CATEGORY = "category";
  private static final String FIELD_POSTURE = "posture";
  private static final String FIELD_SCORE = "score";

  @Override
  public void save(SdqSubmission sdq) throws SdqException {
    sdq.scores()
        .forEach(
            score -> {
              AtomicInteger paramIndex = new AtomicInteger(1);
              jdbcClient
                  .sql(insertSQL())
                  .param(paramIndex.getAndIncrement(), sdq.periodId())
                  .param(paramIndex.getAndIncrement(), sdq.assessor().name())
                  .param(paramIndex.getAndIncrement(), score.statement().name())
                  .param(paramIndex.getAndIncrement(), score.statement().category().name())
                  .param(
                      paramIndex.getAndIncrement(), score.statement().category().posture().name())
                  .param(paramIndex.getAndIncrement(), score.score())
                  .update();
            });
  }

  public int deleteAll() throws SdqException {
    return jdbcClient.sql(deleteAllSQL()).update();
  }

  static String insertSQL() {
    return String.format(
        "INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?)",
        TABLE_NAME,
        FIELD_PERIOD_ID,
        FIELD_ASSESSOR,
        FIELD_STATEMENT,
        FIELD_CATEGORY,
        FIELD_POSTURE,
        FIELD_SCORE);
  }

  static String deleteAllSQL() {
    return String.format("DELETE FROM %s", TABLE_NAME);
  }
}
