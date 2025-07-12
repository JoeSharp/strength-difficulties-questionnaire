package uk.ratracejoe.sdq_analysis.database.repository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq_analysis.database.entity.SdqScoresEntity;
import uk.ratracejoe.sdq_analysis.database.entity.SdqScoresPivot;
import uk.ratracejoe.sdq_analysis.database.tables.SdqResponseTable;
import uk.ratracejoe.sdq_analysis.dto.Assessor;
import uk.ratracejoe.sdq_analysis.dto.Category;
import uk.ratracejoe.sdq_analysis.dto.Posture;
import uk.ratracejoe.sdq_analysis.exception.SdqException;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import static uk.ratracejoe.sdq_analysis.database.repository.RepositoryUtils.handle;
import static uk.ratracejoe.sdq_analysis.database.tables.SdqResponseTable.*;

@Service
@RequiredArgsConstructor
public class SdqResponseRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(SdqResponseRepository.class);
    private final DataSource dataSource;

    public List<SdqScoresPivot> getScores(UUID fileUuid) throws SdqException {
        return handle(dataSource, "getScores", SdqResponseTable.selectScoresPivotSQL(), stmt -> {
            stmt.setString(1, fileUuid.toString());
            List<SdqScoresPivot> sdqScores = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString(FIELD_FILE_UUID));
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
                sdqScores.add(new SdqScoresPivot(uuid, period, assessor, categoryScores, postureScores, total));
            }

            return sdqScores;
        });
    }

    public void recordResponse(SdqScoresEntity score) throws SdqException {
        handle(dataSource, "recordSdq", SdqResponseTable.insertSQL(), stmt -> {
            int rowsUpdated = 0;
            try {
                stmt.setString(1, score.fileUUID().toString());
                stmt.setInt(2, score.period());
                stmt.setString(3, score.assessor().name());
                stmt.setString(4, score.statement().name());
                stmt.setString(5, score.statement().category().name());
                stmt.setString(6, score.statement().category().posture().name());
                stmt.setInt(7, score.score());
                rowsUpdated = stmt.executeUpdate();
                LOGGER.info("Inserted SDQ response to database, rows updated {}", rowsUpdated);
            } catch (Exception e) {
                LOGGER.error("Could not save response", e);
            }
            return rowsUpdated;
        });
    }

    public int deleteAll() throws SdqException {
        return handle(dataSource,
                "deleteAll",
                SdqResponseTable.deleteAllSQL(),
                PreparedStatement::executeUpdate);
    }
}
