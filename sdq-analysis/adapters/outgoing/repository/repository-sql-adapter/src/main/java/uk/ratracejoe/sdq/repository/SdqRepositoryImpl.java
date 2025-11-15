package uk.ratracejoe.sdq.repository;

import static uk.ratracejoe.sdq.repository.RepositoryUtils.handle;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.*;
import uk.ratracejoe.sdq.tables.SdqTable;

@RequiredArgsConstructor
public class SdqRepositoryImpl implements SdqRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(SdqRepositoryImpl.class);
  private final DataSource dataSource;

  public List<SdqScore> getScores(UUID fileId) throws SdqException {
    return handle(
        dataSource,
        "getScores",
        SdqTable.selectScoresSQL(),
        stmt -> {
          stmt.setString(1, fileId.toString());
          List<SdqScore> sdqScores = new ArrayList<>();
          ResultSet rs = stmt.executeQuery();
          while (rs.next()) {
            UUID uuid = UUID.fromString(rs.getString(SdqTable.FIELD_FILE_ID));
            int period = rs.getInt(SdqTable.FIELD_PERIOD_INDEX);
            Assessor assessor = Assessor.valueOf(rs.getString(SdqTable.FIELD_ASSESSOR));
            Statement statement = Statement.valueOf(rs.getString(SdqTable.FIELD_STATEMENT));
            Integer score = rs.getInt(SdqTable.FIELD_SCORE);

            sdqScores.add(new SdqScore(uuid, period, assessor, statement, score));
          }

          return sdqScores;
        });
  }

  public void recordResponse(SdqScore score) throws SdqException {
    handle(
        dataSource,
        "recordSdq",
        SdqTable.insertSQL(),
        stmt -> {
          try {
            AtomicInteger paramIndex = new AtomicInteger(1);
            stmt.setString(paramIndex.getAndIncrement(), score.fileId().toString());
            stmt.setInt(paramIndex.getAndIncrement(), score.period());
            stmt.setString(paramIndex.getAndIncrement(), score.assessor().name());
            stmt.setString(paramIndex.getAndIncrement(), score.statement().name());
            stmt.setString(paramIndex.getAndIncrement(), score.statement().category().name());
            stmt.setString(
                paramIndex.getAndIncrement(), score.statement().category().posture().name());
            stmt.setInt(paramIndex.getAndIncrement(), score.score());
            stmt.executeUpdate();
          } catch (Exception e) {
            LOGGER.error("Could not save response", e);
          }
          return 0;
        });
  }

  public int deleteAll() throws SdqException {
    return handle(
        dataSource, "deleteAll", SdqTable.deleteAllSQL(), PreparedStatement::executeUpdate);
  }
}
