package uk.ratracejoe.sdq.repository;

import static uk.ratracejoe.sdq.tables.GoalBasedOutcomeTable.*;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.GboScore;
import uk.ratracejoe.sdq.tables.GoalBasedOutcomeTable;

@RequiredArgsConstructor
public class GboRepositoryImpl implements GboRepository {
  private final JdbcClient jdbcClient;

  public void save(GboScore domain) {
    String sql = GoalBasedOutcomeTable.insertSQL();

    jdbcClient
        .sql(sql)
        .param(1, domain.fileId())
        .param(2, domain.assessor().name())
        .param(3, domain.periodIndex())
        .param(4, Date.valueOf(domain.periodDate().atZone(ZoneId.systemDefault()).toLocalDate()))
        .param(5, domain.scoreIndex())
        .param(6, domain.score())
        .update();
  }

  @Override
  public int deleteAll() {
    return jdbcClient.sql(GoalBasedOutcomeTable.deleteAllSQL()).update();
  }

  public List<GboScore> getByFileUuid(UUID uuid) {
    String sql = GoalBasedOutcomeTable.getByFileSQL();

    return jdbcClient.sql(sql).param(1, uuid).query((rs, rowNum) -> getFromResultSet(rs)).list();
  }

  private GboScore getFromResultSet(ResultSet rs) throws SQLException {
    return new GboScore(
        rs.getObject(FIELD_CLIENT_ID, UUID.class),
        Assessor.valueOf(rs.getString(FIELD_ASSESSOR)),
        rs.getInt(FIELD_PERIOD_INDEX),
        RepositoryUtils.toInstant(rs.getDate(FIELD_PERIOD_DATE)),
        rs.getInt(FIELD_SCORE_INDEX),
        rs.getInt(FIELD_SCORE));
  }
}
