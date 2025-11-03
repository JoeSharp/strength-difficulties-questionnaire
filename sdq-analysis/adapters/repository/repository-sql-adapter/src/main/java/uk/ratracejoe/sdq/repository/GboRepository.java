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
import uk.ratracejoe.sdq.entity.GboEntity;
import uk.ratracejoe.sdq.entity.GboPivot;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.tables.GoalBasedOutcomeTable;

@RequiredArgsConstructor
public class GboRepository {
  private final DataSource dataSource;

  public void save(GboEntity entity) {
    RepositoryUtils.handle(
        dataSource,
        "saveGbo",
        GoalBasedOutcomeTable.insertSQL(),
        stmt -> {
          LocalDate localDate = LocalDate.ofInstant(entity.periodDate(), ZoneId.systemDefault());
          Date periodDate = Date.valueOf(localDate);

          AtomicInteger paramIndex = new AtomicInteger(1);
          stmt.setString(paramIndex.getAndIncrement(), entity.fileUuid().toString());
          stmt.setString(paramIndex.getAndIncrement(), entity.assessor().name());
          stmt.setInt(paramIndex.getAndIncrement(), entity.periodIndex());
          stmt.setDate(paramIndex.getAndIncrement(), periodDate);
          stmt.setInt(paramIndex.getAndIncrement(), entity.scoreIndex());
          stmt.setInt(paramIndex.getAndIncrement(), entity.score());

          return stmt.executeUpdate();
        });
  }

  public List<GboPivot> getByFileUuid(UUID uuid) throws SdqException {
    return RepositoryUtils.handle(
        dataSource,
        "getGboByFile",
        GoalBasedOutcomeTable.getByFileSQL(),
        stmt -> {
          stmt.setString(1, uuid.toString());
          ResultSet rs = stmt.executeQuery();
          List<GboPivot> results = new ArrayList<>();
          while (rs.next()) {
            results.add(getFromResultSet(rs));
          }
          return results;
        });
  }

  private GboPivot getFromResultSet(ResultSet rs) throws SQLException {
    return new GboPivot(
        UUID.fromString(rs.getString(FIELD_FILE_ID)),
        Assessor.valueOf(rs.getString(FIELD_ASSESSOR)),
        rs.getInt(FIELD_PERIOD_INDEX),
        RepositoryUtils.toInstant(rs.getDate(FIELD_PERIOD_DATE)),
        rs.getInt(FIELD_SCORE_INDEX),
        rs.getInt(FIELD_SCORE));
  }
}
