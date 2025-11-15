package uk.ratracejoe.sdq.repository;

import static uk.ratracejoe.sdq.tables.GoalBasedOutcomeTable.*;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.GboScore;
import uk.ratracejoe.sdq.tables.GoalBasedOutcomeTable;

@RequiredArgsConstructor
public class GboRepositoryImpl implements GboRepository {
  private final DataSource dataSource;

  public void save(GboScore domain) {
    RepositoryUtils.handle(
        dataSource,
        "saveGbo",
        GoalBasedOutcomeTable.insertSQL(),
        stmt -> {
          LocalDate localDate = LocalDate.ofInstant(domain.periodDate(), ZoneId.systemDefault());
          Date periodDate = Date.valueOf(localDate);

          AtomicInteger paramIndex = new AtomicInteger(1);
          stmt.setString(paramIndex.getAndIncrement(), domain.fileId().toString());
          stmt.setString(paramIndex.getAndIncrement(), domain.assessor().name());
          stmt.setInt(paramIndex.getAndIncrement(), domain.periodIndex());
          stmt.setDate(paramIndex.getAndIncrement(), periodDate);
          stmt.setInt(paramIndex.getAndIncrement(), domain.scoreIndex());
          stmt.setInt(paramIndex.getAndIncrement(), domain.score());

          return stmt.executeUpdate();
        });
  }

  public List<GboScore> getByFileUuid(UUID uuid) throws SdqException {
    return RepositoryUtils.handle(
        dataSource,
        "getGboByFile",
        GoalBasedOutcomeTable.getByFileSQL(),
        stmt -> {
          stmt.setString(1, uuid.toString());
          ResultSet rs = stmt.executeQuery();
          List<GboScore> results = new ArrayList<>();
          while (rs.next()) {
            results.add(getFromResultSet(rs));
          }
          return results;
        });
  }

  private GboScore getFromResultSet(ResultSet rs) throws SQLException {
    return new GboScore(
        UUID.fromString(rs.getString(FIELD_FILE_ID)),
        Assessor.valueOf(rs.getString(FIELD_ASSESSOR)),
        rs.getInt(FIELD_PERIOD_INDEX),
        RepositoryUtils.toInstant(rs.getDate(FIELD_PERIOD_DATE)),
        rs.getInt(FIELD_SCORE_INDEX),
        rs.getInt(FIELD_SCORE));
  }
}
