package uk.ratracejoe.sdq.database.repository;

import static uk.ratracejoe.sdq.database.repository.RepositoryUtils.handle;
import static uk.ratracejoe.sdq.database.repository.RepositoryUtils.toInstant;
import static uk.ratracejoe.sdq.database.tables.GoalBasedOutcomeTable.*;
import static uk.ratracejoe.sdq.service.xslx.XslxGboExtractor.NUMBER_SCORES_EXPECTED;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq.database.entity.GboEntity;
import uk.ratracejoe.sdq.database.entity.GboPivot;
import uk.ratracejoe.sdq.database.tables.GoalBasedOutcomeTable;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;

@Service
@RequiredArgsConstructor
public class GboRepository {
  private final DataSource dataSource;

  public void save(GboEntity entity) {
    handle(
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
    return handle(
        dataSource,
        "getGboByFile",
        GoalBasedOutcomeTable.selectScoresPivotSQL(),
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
    Map<Integer, Integer> scores = new HashMap<>();
    for (int p = 1; p <= NUMBER_SCORES_EXPECTED; p++) {
      Integer score = rs.getInt(pivotFieldForScore(p));
      scores.put(p, score);
    }
    return new GboPivot(
        UUID.fromString(rs.getString(FIELD_FILE_ID)),
        Assessor.valueOf(rs.getString(FIELD_ASSESSOR)),
        rs.getInt(FIELD_PERIOD_INDEX),
        toInstant(rs.getDate(FIELD_PERIOD_DATE)),
        scores);
  }
}
