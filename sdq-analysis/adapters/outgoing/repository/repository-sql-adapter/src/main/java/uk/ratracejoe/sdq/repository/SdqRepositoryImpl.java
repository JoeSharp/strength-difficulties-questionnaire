package uk.ratracejoe.sdq.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.sdq.SdqScore;
import uk.ratracejoe.sdq.model.sdq.SdqSubmission;
import uk.ratracejoe.sdq.model.sdq.Statement;

@RequiredArgsConstructor
public class SdqRepositoryImpl implements SdqRepository {
  private final JdbcClient jdbcClient;

  private static final String TABLE_NAME = "sdq";
  private static final String FIELD_PERIOD_ID = "period_id";
  private static final String FIELD_ASSESSOR = "assessor";
  private static final String FIELD_STATEMENT = "statement";
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
                  .param(paramIndex.getAndIncrement(), score.score())
                  .update();
            });
  }

  @Override
  public SdqSubmission get(UUID periodId, Assessor assessor) {
    return SdqSubmission.builder()
        .periodId(periodId)
        .assessor(assessor)
        .scores(getScores(periodId, assessor))
        .build();
  }

  public int deleteAll() throws SdqException {
    return jdbcClient.sql(deleteAllSQL()).update();
  }

  private static String insertSQL() {
    return String.format(
        "INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
        TABLE_NAME, FIELD_PERIOD_ID, FIELD_ASSESSOR, FIELD_STATEMENT, FIELD_SCORE);
  }

  private static String getByPeriodAndAssessorIdSQL() {
    return String.format(
        "SELECT %s, %s FROM %s WHERE %s = ? AND %s = ?",
        FIELD_STATEMENT, FIELD_SCORE, TABLE_NAME, FIELD_PERIOD_ID, FIELD_ASSESSOR);
  }

  private static String deleteAllSQL() {
    return String.format("DELETE FROM %s", TABLE_NAME);
  }

  private List<SdqScore> getScores(UUID periodId, Assessor assessor) {
    return jdbcClient
        .sql(getByPeriodAndAssessorIdSQL())
        .param(1, periodId)
        .param(2, assessor.name())
        .query(
            (rs, rowNum) ->
                SdqScore.builder()
                    .statement(
                        Optional.ofNullable(rs.getString(1)).map(Statement::valueOf).orElse(null))
                    .score(rs.getInt(2))
                    .build())
        .list();
  }
}
