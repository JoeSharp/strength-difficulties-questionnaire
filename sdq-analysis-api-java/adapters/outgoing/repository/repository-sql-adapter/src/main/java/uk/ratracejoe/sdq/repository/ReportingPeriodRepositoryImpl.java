package uk.ratracejoe.sdq.repository;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.model.ReportingPeriod;

@RequiredArgsConstructor
public class ReportingPeriodRepositoryImpl implements ReportingPeriodRepository {
  private final JdbcClient jdbcClient;

  @Override
  public void save(ReportingPeriod period) {
    jdbcClient
        .sql(
            """
          INSERT INTO reporting_period
            (period_id, client_id, period_date)
          VALUES
            (:periodId, :clientId, :period)""")
        .params(
            Map.of(
                "periodId",
                period.periodId(),
                "clientId",
                period.clientId(),
                "period",
                Date.valueOf(period.period())))
        .update();
  }

  @Override
  public List<ReportingPeriod> getForClient(UUID clientId) {
    return jdbcClient
        .sql(
            """
                SELECT
                  period_id, client_id, period_date
                FROM
                  reporting_period
                WHERE
                  client_id = :clientId""")
        .param("clientId", clientId)
        .query(
            (rs, rowNum) ->
                ReportingPeriod.builder()
                    .periodId(rs.getObject("period_id", UUID.class))
                    .period(rs.getDate("period_date").toLocalDate())
                    .clientId(rs.getObject("client_id", UUID.class))
                    .build())
        .list();
  }

  @Override
  public int deleteAll() {
    return jdbcClient.sql("DELETE FROM reporting_period").update();
  }
}
