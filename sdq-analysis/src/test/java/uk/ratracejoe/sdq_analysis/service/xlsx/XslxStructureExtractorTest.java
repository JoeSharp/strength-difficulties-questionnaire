package uk.ratracejoe.sdq_analysis.service.xlsx;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import uk.ratracejoe.sdq_analysis.dto.DemographicField;
import uk.ratracejoe.sdq_analysis.exception.SdqException;
import uk.ratracejoe.sdq_analysis.service.xslx.XslxStructureExtractor;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.ratracejoe.sdq_analysis.Utils.workbookLoaded;

class XslxStructureExtractorTest {
    @Test
    void extractsStructure() throws IOException, SdqException {
        // Given
        XslxStructureExtractor extractor = new XslxStructureExtractor();
        Workbook workbook = workbookLoaded();

        // When
        var result = extractor.extractDemographicOptions(workbook);

        // Then
        assertThat(result).containsEntry(DemographicField.Gender,
                List.of("Male", "Female", "Non-Binary", "Other", "Prefer Not to Say"));
        assertThat(result).containsEntry(DemographicField.InterventionType,
                List.of("CCPT", "CPRT", "PTP", "IA"));
    }

}