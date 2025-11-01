package uk.ratracejoe.sdq_analysis.service.xlsx;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.ratracejoe.sdq_analysis.Utils.workbookLoaded;

import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import uk.ratracejoe.sdq_analysis.dto.SdqPeriod;
import uk.ratracejoe.sdq_analysis.service.xslx.XslxSdqExtractor;

class XslxSdqExtractorTest {
  @Test
  void sdqExtracted() throws IOException {
    // Given
    XslxSdqExtractor extractor = new XslxSdqExtractor();
    Workbook workbook = workbookLoaded();

    // When
    List<SdqPeriod> result = extractor.parse(workbook);

    // Then
    assertThat(result).hasSize(9);
  }
}
