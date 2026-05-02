package uk.ratracejoe.sdq.repository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.demographics.DemographicFilter;
import uk.ratracejoe.sdq.model.sdq.SdqProgress;
import uk.ratracejoe.sdq.model.sdq.SdqScore;
import uk.ratracejoe.sdq.model.sdq.SdqSubmission;

@RequiredArgsConstructor
public class SdqRepositoryImpl implements SdqRepository {
  private final JdbcClient jdbcClient;

  private final StatementRepository statementRepository;

  @Override
  public void save(SdqSubmission sdq) throws SdqException {
    sdq.scores()
        .forEach(
            score ->
                jdbcClient
                    .sql(
                        """
                INSERT INTO sdq
                  (period_id, assessor, statement, score)
                VALUES
                  (:periodId, :assessor, :statement, :score)""")
                    .param("periodId", sdq.periodId())
                    .param("assessor", sdq.assessor().name())
                    .param("statement", score.statement().key())
                    .param("score", score.score())
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

  @Override
  public List<SdqSubmission> getFilteredSdqs(
      Assessor assessor,
      String category,
      List<DemographicFilter> filters,
      LocalDate from,
      LocalDate to) {
    return null;
  }

  private List<SdqScore> getScores(UUID periodId, Assessor assessor) {
    return jdbcClient
        .sql(
            """
          SELECT
            s.statement AS statement_key,
            s.score as score
          FROM
            sdq s
          INNER JOIN sdq_statement st
          ON s.statement = st.statement_key
          WHERE period_id = ? AND assessor = ?
        """)
        .param(1, periodId)
        .param(2, assessor.name())
        .query(
            (rs, rowNum) ->
                SdqScore.builder()
                    .statement(statementRepository.getStatement(rs.getString("statement_key")))
                    .score(rs.getInt("score"))
                    .build())
        .list();
  }
}
