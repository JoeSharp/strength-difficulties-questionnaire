package uk.ratracejoe.sdq.xslx;

import static uk.ratracejoe.sdq.xslx.Utils.workbookLoaded;

import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Workbook;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.DemographicField;

class XslxStructureExtractorTest {
  @Test
  void extractsStructure() throws IOException, SdqException {
    // Given
    XslxStructureExtractor extractor = new XslxStructureExtractor();
    Workbook workbook = workbookLoaded();

    // When
    var result = extractor.extractDemographicOptions(workbook);

    // Then
    Assertions.assertThat(result)
        .containsEntry(
            DemographicField.Gender,
            List.of("Male", "Female", "Non-Binary", "Other", "Prefer Not to Say"));
    Assertions.assertThat(result)
        .containsEntry(DemographicField.InterventionType, List.of("CCPT", "CPRT", "PTP", "IA"));
  }
}
