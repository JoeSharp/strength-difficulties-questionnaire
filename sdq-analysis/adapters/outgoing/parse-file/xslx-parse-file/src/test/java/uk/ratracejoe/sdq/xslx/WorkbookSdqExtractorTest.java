package uk.ratracejoe.sdq.xslx;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.ratracejoe.sdq.xslx.Utils.workbookLoaded;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import uk.ratracejoe.sdq.model.sdq.SdqReportingPeriod;

class WorkbookSdqExtractorTest {
  @Test
  void sdqExtracted() throws IOException {
    // Given
    UUID clientId = UUID.randomUUID();
    WorkbookSdqExtractor extractor = new WorkbookSdqExtractor();
    Workbook workbook = workbookLoaded();

    // When
    List<SdqReportingPeriod> result = extractor.parse(clientId, workbook);

    // Then
    assertThat(result).hasSize(9);
  }
}
