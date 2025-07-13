package uk.ratracejoe.sdq_analysis.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.ratracejoe.sdq_analysis.config.DbConfig;
import uk.ratracejoe.sdq_analysis.database.tables.*;
import uk.ratracejoe.sdq_analysis.dto.DatabaseStructure;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@Component
@RequiredArgsConstructor
public class DatabaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseService.class);

    private final DataSource dataSource;
    private final DbConfig dbConfig;

    public boolean databaseExists() {
        return Files.exists(Path.of(dbConfig.getDatabaseFile()));
    }

    public void createDatabase(DatabaseStructure databaseStructure) {
        try (Connection conn = dataSource.getConnection()) {
            if (conn != null) {
                LOGGER.info("Connected to SQLite.");
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(DemographicOptionTable.createTableSQL());
                    stmt.executeUpdate(ClientFileTable.createTableSQL());
                    stmt.executeUpdate(SdqTable.createTableSQL());
                    stmt.executeUpdate(InterventionTypeTable.createTableSQL());
                    stmt.executeUpdate(GoalBasedOutcomeTable.createTableSQL());
                }
                createDemographicOptions(conn, databaseStructure);
                LOGGER.info("Database created for SDQ");
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void createDemographicOptions(Connection connection, DatabaseStructure databaseStructure) {
        databaseStructure.demographics().forEach((key, value) -> value.forEach(optionValue -> {
            try {
                PreparedStatement stmt = connection.prepareStatement(DemographicOptionTable.insertOptionSQL());
                stmt.setString(1, key.name());
                stmt.setString(2, optionValue);
                stmt.executeUpdate();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
            }
        }));
    }

    public void deleteDatabase() {
        try {
            Files.delete(Path.of(dbConfig.getDatabaseFile()));
            LOGGER.info("Deleted existing test database {}", dbConfig.getDatabaseFile());
        } catch (Exception e) {
            LOGGER.info("Couldn't delete database, but that may be fine");
        }
    }
}
