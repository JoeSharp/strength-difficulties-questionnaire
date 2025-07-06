package uk.ratracejoe.sdq_analysis.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.ratracejoe.sdq_analysis.config.DbConfig;
import uk.ratracejoe.sdq_analysis.database.repository.ClientFileRepository;
import uk.ratracejoe.sdq_analysis.database.repository.SdqResponseRepository;
import uk.ratracejoe.sdq_analysis.database.tables.ClientFileTable;
import uk.ratracejoe.sdq_analysis.database.tables.DemographicOptionTable;
import uk.ratracejoe.sdq_analysis.database.tables.InterventionTypeTable;
import uk.ratracejoe.sdq_analysis.database.tables.SdqResponseTable;
import uk.ratracejoe.sdq_analysis.dto.DatabaseStructure;
import uk.ratracejoe.sdq_analysis.dto.DeleteAllResponse;
import uk.ratracejoe.sdq_analysis.exception.SdqException;
import uk.ratracejoe.sdq_analysis.service.xslx.XslxStructureExtractor;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
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

    private final XslxStructureExtractor structureExtractor;
    private final ClientFileRepository fileRepository;
    private final SdqResponseRepository sdqResponseRepository;
    private final DataSource dataSource;
    private final DbConfig dbConfig;

    public boolean databaseExists() {
        return Files.exists(Path.of(dbConfig.getDatabaseFile()));
    }

    public DatabaseStructure createDatabase(InputStream file) throws SdqException, IOException {
        Workbook workbook = new XSSFWorkbook(file);

        var demographics = structureExtractor.extractDemographicOptions(workbook);
        DatabaseStructure structure = new DatabaseStructure(demographics);

        createDatabase(structure);

        return structure;
    }

    private void createDatabase(DatabaseStructure databaseStructure) {
        try (Connection conn = dataSource.getConnection()) {
            if (conn != null) {
                LOGGER.info("Connected to SQLite.");
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(DemographicOptionTable.createTableSQL());
                    stmt.executeUpdate(ClientFileTable.createTableSQL());
                    stmt.executeUpdate(SdqResponseTable.createTableSQL());
                    stmt.executeUpdate(InterventionTypeTable.createTableSQL());
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

    public DeleteAllResponse clearDatabase() throws SdqException {
        int filesDeleted = fileRepository.deleteAll();
        int responsesDeleted = sdqResponseRepository.deleteAll();
        return new DeleteAllResponse(filesDeleted, responsesDeleted);
    }

    public void deleteDatabase() {
        try {
            Files.delete(Path.of(dbConfig.getDatabaseFile()));
            LOGGER.info("Deleted existing test database " + dbConfig.getDatabaseFile());
        } catch (Exception e) {
            LOGGER.info("Couldn't delete database, but that may be fine");
        }
    }
}
