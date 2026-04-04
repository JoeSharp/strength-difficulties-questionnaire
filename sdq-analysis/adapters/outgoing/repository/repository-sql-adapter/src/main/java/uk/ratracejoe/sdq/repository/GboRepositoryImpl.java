package uk.ratracejoe.sdq.repository;

import static uk.ratracejoe.sdq.tables.GoalBasedOutcomeTable.*;

import java.sql.Date;
import java.time.ZoneId;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.model.GboSubmission;
import uk.ratracejoe.sdq.tables.GoalBasedOutcomeTable;

@RequiredArgsConstructor
public class GboRepositoryImpl implements GboRepository {
  private final JdbcClient jdbcClient;

  public void save(GboSubmission domain) {
    String sql = GoalBasedOutcomeTable.insertSQL();

    domain
        .scores()
        .forEach(
            score ->
                jdbcClient
                    .sql(sql)
                    .param(1, domain.clientId())
                    .param(2, domain.assessor().name())
                    .param(
                        3,
                        Date.valueOf(domain.period().atZone(ZoneId.systemDefault()).toLocalDate()))
                    .param(4, score.scoreIndex())
                    .param(5, score.score())
                    .update());
  }

  @Override
  public int deleteAll() {
    return jdbcClient.sql(GoalBasedOutcomeTable.deleteAllSQL()).update();
  }
}
