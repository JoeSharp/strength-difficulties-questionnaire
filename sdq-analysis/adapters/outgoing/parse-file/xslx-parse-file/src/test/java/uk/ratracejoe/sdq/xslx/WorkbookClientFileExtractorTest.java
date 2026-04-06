package uk.ratracejoe.sdq.xslx;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.ratracejoe.sdq.xslx.Utils.workbookLoaded;

import java.io.IOException;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import uk.ratracejoe.sdq.model.ParsedFile;

class WorkbookClientFileExtractorTest {
  @Test
  void goalsExtracted() throws IOException {
    // Given
    WorkbookClientFileExtractor fileParser = new WorkbookClientFileExtractor();
    Workbook workbook = workbookLoaded();

    // When
    ParsedFile result = fileParser.extract("My Name", workbook);

    // Then
    assertThat(result.goals()).hasSize(4);
  }
}
