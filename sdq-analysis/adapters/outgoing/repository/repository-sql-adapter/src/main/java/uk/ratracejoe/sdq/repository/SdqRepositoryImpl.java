package uk.ratracejoe.sdq.repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.*;
import uk.ratracejoe.sdq.tables.SdqTable;

@RequiredArgsConstructor
public class SdqRepositoryImpl implements SdqRepository {
  private final JdbcClient jdbcClient;

  public List<SdqScore> getScores(UUID fileId) throws SdqException {
    return jdbcClient
        .sql(SdqTable.selectScoresSQL())
        .param(1, fileId)
        .query(
            (rs, rowNum) -> {
              UUID uuid = rs.getObject(SdqTable.FIELD_CLIENT_ID, UUID.class);
              int period = rs.getInt(SdqTable.FIELD_PERIOD_INDEX);
              Assessor assessor = Assessor.valueOf(rs.getString(SdqTable.FIELD_ASSESSOR));
              Statement statement = Statement.valueOf(rs.getString(SdqTable.FIELD_STATEMENT));
              Integer score = rs.getInt(SdqTable.FIELD_SCORE);

              return new SdqScore(uuid, period, assessor, statement, score);
            })
        .list();
  }

  public void recordResponse(SdqScore score) throws SdqException {
    AtomicInteger paramIndex = new AtomicInteger(1);
    jdbcClient
        .sql(SdqTable.insertSQL())
        .param(paramIndex.getAndIncrement(), score.fileId())
        .param(paramIndex.getAndIncrement(), score.period())
        .param(paramIndex.getAndIncrement(), score.assessor().name())
        .param(paramIndex.getAndIncrement(), score.statement().name())
        .param(paramIndex.getAndIncrement(), score.statement().category().name())
        .param(paramIndex.getAndIncrement(), score.statement().category().posture().name())
        .param(paramIndex.getAndIncrement(), score.score())
        .update();
  }

  public int deleteAll() throws SdqException {
    return jdbcClient.sql(SdqTable.deleteAllSQL()).update();
  }
}
