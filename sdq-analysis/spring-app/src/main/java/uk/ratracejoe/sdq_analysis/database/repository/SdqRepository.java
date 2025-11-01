package uk.ratracejoe.sdq_analysis.database.repository;

import static uk.ratracejoe.sdq_analysis.database.repository.RepositoryUtils.handle;
import static uk.ratracejoe.sdq_analysis.database.tables.SdqTable.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq_analysis.database.entity.SdqPivot;
import uk.ratracejoe.sdq_analysis.database.entity.SdqScoresEntity;
import uk.ratracejoe.sdq_analysis.database.tables.SdqTable;
import uk.ratracejoe.sdq_analysis.dto.Assessor;
import uk.ratracejoe.sdq_analysis.dto.Category;
import uk.ratracejoe.sdq_analysis.dto.Posture;
import uk.ratracejoe.sdq_analysis.exception.SdqException;

@Service
@RequiredArgsConstructor
public class SdqRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(SdqRepository.class);
  private final DataSource dataSource;

  public List<SdqPivot> getScores(UUID fileUuid) throws SdqException {
    return handle(
        dataSource,
        "getScores",
        SdqTable.selectScoresPivotSQL(),
        stmt -> {
          stmt.setString(1, fileUuid.toString());
          List<SdqPivot> sdqScores = new ArrayList<>();
          ResultSet rs = stmt.executeQuery();
          while (rs.next()) {
            UUID uuid = UUID.fromString(rs.getString(FIELD_FILE_ID));
            int period = rs.getInt(FIELD_PERIOD_INDEX);
            Assessor assessor = Assessor.valueOf(rs.getString(FIELD_ASSESSOR));
            Map<Category, Integer> categoryScores = new HashMap<>();
            for (Category c : Category.values()) {
              Integer score = rs.getInt(c.name());
              categoryScores.put(c, score);
            }
            Map<Posture, Integer> postureScores = new HashMap<>();
            for (Posture p : Posture.values()) {
              Integer score = rs.getInt(p.name());
              postureScores.put(p, score);
            }
            int total = rs.getInt(FIELD_TOTAL);
            sdqScores.add(
                new SdqPivot(uuid, period, assessor, categoryScores, postureScores, total));
          }

          return sdqScores;
        });
  }

  public void recordResponse(SdqScoresEntity score) throws SdqException {
    handle(
        dataSource,
        "recordSdq",
        SdqTable.insertSQL(),
        stmt -> {
          try {
            AtomicInteger paramIndex = new AtomicInteger(1);
            stmt.setString(paramIndex.getAndIncrement(), score.fileUUID().toString());
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
