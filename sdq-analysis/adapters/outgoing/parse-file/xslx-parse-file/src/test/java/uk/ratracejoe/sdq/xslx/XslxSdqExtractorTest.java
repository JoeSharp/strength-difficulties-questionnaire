package uk.ratracejoe.sdq.xslx;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.ratracejoe.sdq.xslx.Utils.workbookLoaded;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import uk.ratracejoe.sdq.model.SdqScore;

class XslxSdqExtractorTest {
  @Test
  void sdqExtracted() throws IOException {
    // Given
    UUID fileId = UUID.randomUUID();
    XslxSdqExtractor extractor = new XslxSdqExtractor();
    Workbook workbook = workbookLoaded();

    // When
    List<SdqScore> result = extractor.parse(fileId, workbook);

    // Then
    assertThat(result).hasSize(9);
  }
}
