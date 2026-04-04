package uk.ratracejoe.sdq.repository;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
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

  private static String insertSQL() {
    return String.format(
        "INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)",
        TABLE_NAME, FIELD_PERIOD_ID, FIELD_CLIENT_ID, FIELD_PERIOD_DATE);
  }

  private static String selectByClientSQL() {
    return String.format(
        "SELECT %s, %s, %s FROM %s WHERE %s = ?",
        FIELD_PERIOD_ID, FIELD_CLIENT_ID, FIELD_PERIOD_DATE, TABLE_NAME, FIELD_CLIENT_ID);
  }

  @Override
  public void save(ReportingPeriod period) {
    jdbcClient
        .sql(insertSQL())
        .param(1, period.periodId())
        .param(2, period.clientId())
        .param(3, Date.valueOf(period.period().atZone(ZoneId.systemDefault()).toLocalDate()))
        .update();
  }

  @Override
  public List<ReportingPeriod> getForClient(UUID clientId) {
    return jdbcClient
        .sql(selectByClientSQL())
        .param(1, clientId)
        .query(
            (rs, rowNum) -> {
              LocalDate date = rs.getDate(FIELD_PERIOD_DATE).toLocalDate();
              Instant periodDate = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
              return ReportingPeriod.builder()
                  .period(periodDate)
                  .clientId(rs.getObject(FIELD_CLIENT_ID, UUID.class))
                  .periodId(rs.getObject(FIELD_PERIOD_ID, UUID.class))
                  .build();
            })
        .list();
  }

  @Override
  public void deleteAll() {}
}
