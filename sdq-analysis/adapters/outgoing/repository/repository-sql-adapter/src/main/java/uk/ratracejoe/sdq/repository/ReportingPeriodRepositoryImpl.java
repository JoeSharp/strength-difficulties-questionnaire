package uk.ratracejoe.sdq.repository;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.model.ReportingPeriod;

@RequiredArgsConstructor
public class ReportingPeriodRepositoryImpl implements ReportingPeriodRepository {
  private final JdbcClient jdbcClient;
  private static final String TABLE_NAME = "reporting_period";
  private static final String FIELD_PERIOD_ID = "period_id";
  private static final String FIELD_CLIENT_ID = "client_id";
  private static final String FIELD_PERIOD_DATE = "period_date";

  @Override
  public void save(ReportingPeriod period) {
    AtomicInteger paramIndex = new AtomicInteger(1);
    jdbcClient
        .sql(
            String.format(
                "INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)",
                TABLE_NAME, FIELD_PERIOD_ID, FIELD_CLIENT_ID, FIELD_PERIOD_DATE))
        .param(paramIndex.getAndIncrement(), period.periodId())
        .param(paramIndex.getAndIncrement(), period.clientId())
        .param(
            paramIndex.getAndIncrement(),
            Date.valueOf(period.period()))
        .update();
  }

  @Override
  public List<ReportingPeriod> getForClient(UUID clientId) {
    return jdbcClient
        .sql(
            String.format(
                "SELECT %s, %s, %s FROM %s WHERE %s = ?",
                FIELD_PERIOD_ID, FIELD_CLIENT_ID, FIELD_PERIOD_DATE, TABLE_NAME, FIELD_CLIENT_ID))
        .param(1, clientId)
        .query(
            (rs, rowNum) -> ReportingPeriod.builder()
                .period(rs.getDate(FIELD_PERIOD_DATE).toLocalDate())
                .clientId(rs.getObject(FIELD_CLIENT_ID, UUID.class))
                .periodId(rs.getObject(FIELD_PERIOD_ID, UUID.class))
                .build())
        .list();
  }

  @Override
  public int deleteAll() {
    return jdbcClient.sql(String.format("DELETE FROM %s", TABLE_NAME)).update();
  }
}
