package uk.ratracejoe.sdq.service.xlsx;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.ratracejoe.sdq.Utils.workbookLoaded;

import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.DemographicField;
import uk.ratracejoe.sdq.service.xslx.XslxStructureExtractor;

class XslxStructureExtractorTest {
  @Test
  void extractsStructure() throws IOException, SdqException {
    // Given
    XslxStructureExtractor extractor = new XslxStructureExtractor();
    Workbook workbook = workbookLoaded();

    // When
    var result = extractor.extractDemographicOptions(workbook);

    // Then
    assertThat(result)
        .containsEntry(
            DemographicField.Gender,
            List.of("Male", "Female", "Non-Binary", "Other", "Prefer Not to Say"));
    assertThat(result)
        .containsEntry(DemographicField.InterventionType, List.of("CCPT", "CPRT", "PTP", "IA"));
  }
}
