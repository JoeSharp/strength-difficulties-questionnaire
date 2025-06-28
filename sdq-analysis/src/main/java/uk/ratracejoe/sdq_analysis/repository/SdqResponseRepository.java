package uk.ratracejoe.sdq_analysis.repository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq_analysis.dto.SdqPeriod;
import uk.ratracejoe.sdq_analysis.dto.UploadFile;
import uk.ratracejoe.sdq_analysis.exception.SdqException;

import javax.sql.DataSource;
import java.util.List;

import static uk.ratracejoe.sdq_analysis.repository.AbstractRepository.handle;

@Service
@RequiredArgsConstructor
public class SdqResponseRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(SdqResponseRepository.class);
    private final DataSource dataSource;

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
                            stmt.setInt(5, response.score());
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
