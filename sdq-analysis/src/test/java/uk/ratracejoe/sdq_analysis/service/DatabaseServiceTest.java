package uk.ratracejoe.sdq_analysis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.sqlite.SQLiteDataSource;
import uk.ratracejoe.sdq_analysis.Utils;
import uk.ratracejoe.sdq_analysis.config.DbConfig;
import uk.ratracejoe.sdq_analysis.exception.SdqException;
import uk.ratracejoe.sdq_analysis.service.DatabaseService;
import uk.ratracejoe.sdq_analysis.service.xslx.XslxStructureExtractor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DatabaseServiceTest {
    @Autowired
    private DatabaseService service;

    @Autowired
    private DbConfig dbConfig;

    @BeforeEach
    void beforeEach() {
        service.deleteDatabase();
    }

    @Test
    void databaseDoesntExist() {
        // When
        var result = service.databaseExists();

        // Then
        assertThat(result).isFalse();
    }
    @Test
    void databaseCreates() throws SdqException, IOException {
        // Given
        var workbook = Utils.workbookStream();

        // When
        var structure = service.createDatabase(workbook);
        var exists = service.databaseExists();

        // Then...don't throw exception right?
        assertThat(structure).isNotNull();
        assertThat(exists).isTrue();
    }
}
