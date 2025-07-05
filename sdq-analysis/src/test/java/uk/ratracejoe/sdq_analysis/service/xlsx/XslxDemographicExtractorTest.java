package uk.ratracejoe.sdq_analysis.service.xlsx;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import uk.ratracejoe.sdq_analysis.dto.ClientFile;
import uk.ratracejoe.sdq_analysis.exception.SdqException;
import uk.ratracejoe.sdq_analysis.service.xslx.XslxDemographicExtractor;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.ratracejoe.sdq_analysis.Utils.XLSX_TEST_FILE;
import static uk.ratracejoe.sdq_analysis.Utils.workbookLoaded;

class XslxDemographicExtractorTest {
    @Test
    void parseTestFile() throws SdqException, IOException {
        // Given
        XslxDemographicExtractor extractor = new XslxDemographicExtractor();
        Workbook workbook = workbookLoaded();

        // When
        ClientFile file = extractor.parse(workbook, XLSX_TEST_FILE);

        // Then
        assertThat(file)
                .extracting(ClientFile::filename)
                .isEqualTo(XLSX_TEST_FILE);
        assertThat(file)
                .extracting(ClientFile::gender)
                .isEqualTo("Male");
        assertThat(file)
                .extracting(ClientFile::ethnicity)
                .isEqualTo("White British");
        assertThat(file)
                .extracting(ClientFile::aces)
                .isEqualTo(3);
    }
}
