package uk.ratracejoe.sdq.repository;

import java.time.LocalDate;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.sdq.SdqProgress;
import uk.ratracejoe.sdq.model.sdq.SdqScore;
import uk.ratracejoe.sdq.model.sdq.SdqSubmission;
import uk.ratracejoe.sdq.model.sdq.Statement;

@RequiredArgsConstructor
public class SdqRepositoryImpl implements SdqRepository {
  private final JdbcClient jdbcClient;

  @Override
  public void save(SdqSubmission sdq) throws SdqException {
    sdq.scores()
        .forEach(
            score ->
                jdbcClient
                    .sql(
                        """
                INSERT INTO sdq
                  (period_id, assessor, category, statement, score)
                VALUES
                  (:periodId, :assessor, :category, :statement, :score)""")
                    .params(
                        Map.of(
                            "periodId",
                            sdq.periodId(),
                            "assessor",
                            sdq.assessor().name(),
                            "category",
                            score.statement().category().name(),
                            "statement",
                            score.statement().name(),
                            "score",
                            score.score()))
                    .update());
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
    return jdbcClient.sql("DELETE FROM sdq").update();
  }

  @Override
  public List<SdqProgress> getSdqProgress(Assessor assessor, LocalDate from, LocalDate to) {
    return Collections.emptyList();
  }

  private List<SdqScore> getScores(UUID periodId, Assessor assessor) {
    return jdbcClient
        .sql("SELECT statement, score FROM sdq WHERE period_id = ? AND assessor = ?")
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
