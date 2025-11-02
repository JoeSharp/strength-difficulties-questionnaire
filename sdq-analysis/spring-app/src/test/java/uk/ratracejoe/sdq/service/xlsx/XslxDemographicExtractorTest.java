package uk.ratracejoe.sdq.service.xlsx;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.ratracejoe.sdq.Utils.XLSX_TEST_FILE;
import static uk.ratracejoe.sdq.Utils.workbookLoaded;

import java.io.IOException;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.ClientFile;
import uk.ratracejoe.sdq.service.xslx.XslxDemographicExtractor;

class XslxDemographicExtractorTest {
  @Test
  void parseTestFile() throws SdqException, IOException {
    // Given
    XslxDemographicExtractor extractor = new XslxDemographicExtractor();
    Workbook workbook = workbookLoaded();

    // When
    ClientFile file = extractor.parse(workbook, XLSX_TEST_FILE);

    // Then
    assertThat(file).extracting(ClientFile::filename).isEqualTo(XLSX_TEST_FILE);
    assertThat(file).extracting(ClientFile::gender).isEqualTo("Male");
    assertThat(file).extracting(ClientFile::ethnicity).isEqualTo("White British");
    assertThat(file).extracting(ClientFile::aces).isEqualTo(3);
  }
}
