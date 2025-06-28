package uk.ratracejoe.sdq_analysis.repository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
@RequiredArgsConstructor
public class DatabaseCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseCreator.class);

    private final DataSource dataSource;

    @PostConstruct
    public void postConstruct() {
        this.createDatabase(dataSource);
    }

    public void createDatabase(DataSource dataSource) {

        try (Connection conn = dataSource.getConnection()) {
            if (conn != null) {
                LOGGER.info("Connected to SQLite.");
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(UploadFileTable.createTableSQL());
                    stmt.executeUpdate(SdqResponseTable.createTableSQL());
                }
                LOGGER.info("Database created for SDQ");
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

    }
}
