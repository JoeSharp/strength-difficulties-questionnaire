package uk.ratracejoe.sdq.xslx;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.ratracejoe.sdq.xslx.Utils.workbookLoaded;

import java.io.IOException;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.SdqClient;

class XslxDemographicExtractorTest {
  @Test
  void parseTestFile() throws SdqException, IOException {
    // Given
    XslxDemographicExtractor extractor = new XslxDemographicExtractor();
    Workbook workbook = workbookLoaded();

    // When
    SdqClient file = extractor.parse(workbook, Utils.XLSX_TEST_FILE);

    // Then
    assertThat(file).extracting(SdqClient::codeName).isEqualTo(Utils.XLSX_TEST_FILE);
    assertThat(file).extracting(SdqClient::gender).isEqualTo("Male");
    assertThat(file).extracting(SdqClient::ethnicity).isEqualTo("White British");
    assertThat(file).extracting(SdqClient::aces).isEqualTo(3);
  }
}
