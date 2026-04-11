package uk.ratracejoe.sdq.xslx;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.ratracejoe.sdq.xslx.Utils.workbookLoaded;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.ParsedFile;
import uk.ratracejoe.sdq.model.gbo.GboSubmission;

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
    List<GboSubmission> schoolSubmissions =
        result.gbo().stream()
            .filter(b -> Assessor.School.equals(b.assessor()))
            .sorted(Comparator.comparing(GboSubmission::period))
            .toList();
    assertThat(schoolSubmissions)
        .extracting(GboSubmission::score)
        .containsExactly(2, 4, 8, 5, 3, 5, 9, 2);
    assertThat(schoolSubmissions.getFirst())
        .extracting(GboSubmission::period)
        .satisfies(
            d -> {
              assertThat(d.getYear()).isEqualTo(2025);
              assertThat(d.getMonthValue()).isEqualTo(8);
              assertThat(d.getDayOfMonth()).isEqualTo(11);
            });
  }
}
