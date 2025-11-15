package uk.ratracejoe.sdq.xslx;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.ratracejoe.sdq.xslx.Utils.workbookLoaded;

import java.io.IOException;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.ClientFile;

class XslxDemographicExtractorTest {
  @Test
  void parseTestFile() throws SdqException, IOException {
    // Given
    XslxDemographicExtractor extractor = new XslxDemographicExtractor();
    Workbook workbook = workbookLoaded();

    // When
    ClientFile file = extractor.parse(workbook, Utils.XLSX_TEST_FILE);

    // Then
    assertThat(file).extracting(ClientFile::filename).isEqualTo(Utils.XLSX_TEST_FILE);
    assertThat(file).extracting(ClientFile::gender).isEqualTo("Male");
    assertThat(file).extracting(ClientFile::ethnicity).isEqualTo("White British");
    assertThat(file).extracting(ClientFile::aces).isEqualTo(3);
  }
}
