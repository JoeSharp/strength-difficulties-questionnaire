package uk.ratracejoe.sdq_analysis.repository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq_analysis.dto.*;
import uk.ratracejoe.sdq_analysis.exception.SdqException;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.*;

import static uk.ratracejoe.sdq_analysis.repository.AbstractRepository.handle;
import static uk.ratracejoe.sdq_analysis.repository.SdqResponseTable.*;

@Service
@RequiredArgsConstructor
public class SdqResponseRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(SdqResponseRepository.class);
    private final DataSource dataSource;

    public List<SdqScores> getScores() throws SdqException {
        return handle(dataSource, "getScores", SdqResponseTable.selectScoresSQL(), stmt -> {
            List<SdqScores> sdqScores = new ArrayList<>();
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
                sdqScores.add(new SdqScores(uuid, period, assessor, categoryScores, postureScores, total));
            }

            return sdqScores;
        });
    }

    public void recordResponse(UploadFile file,
                               List<SdqPeriod> periods) throws SdqException {
        handle(dataSource, "recordSdq", SdqResponseTable.insertSQL(), stmt -> {
            periods.forEach(period ->
                    period.responses().forEach((key, value) -> value.forEach(response -> {
                        try {
                            stmt.setString(1, file.uuid().toString());
                            stmt.setInt(2, period.periodIndex());
                            stmt.setString(3, key.name());
                            stmt.setString(4, response.statement().name());
                            stmt.setString(5, response.statement().category().name());
                            stmt.setString(6, response.statement().category().posture().name());
                            stmt.setInt(7, response.score());
                            int rowsUpdated = stmt.executeUpdate();
                            LOGGER.info("Inserted SDQ response to database, rows updated {}", rowsUpdated);
                        } catch (Exception e) {
                            LOGGER.error("Could not save response", e);
                        }
                    }))
            );

            return 0;
        });
    }
}
