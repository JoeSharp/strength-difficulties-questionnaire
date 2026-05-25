package uk.ratracejoe.sdq.xslx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;
import static uk.ratracejoe.sdq.xslx.Utils.*;

import java.io.IOException;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.SdqClient;
import uk.ratracejoe.sdq.model.demographics.*;

class WorkbookDemographicExtractorTest {
  @Test
  void parseOriginalTestFile() throws SdqException, IOException {
    // Given
    WorkbookDemographicExtractor extractor = new WorkbookDemographicExtractor();
    Workbook workbook = workbookLoaded(XLSX_TEST_FILE_1);

    // When
    SdqClient file = extractor.parse(workbook, "original guy");

    // Then
    assertThat(file).extracting(SdqClient::codeName).isEqualTo("original guy");
    assertThat(file).extracting(SdqClient::gender).isEqualTo(Gender.MALE);
    assertThat(file).extracting(SdqClient::ethnicity).isEqualTo(Ethnicity.WHITE_BRITISH);
    assertThat(file).extracting(SdqClient::aces).isEqualTo(3);
  }

  @Test
  void parseRevisedTestFile() throws SdqException, IOException {
    // Given
    WorkbookDemographicExtractor extractor = new WorkbookDemographicExtractor();
    Workbook workbook = workbookLoaded(XLSX_TEST_FILE_REVISED);

    // When
    SdqClient file = extractor.parse(workbook, "revised");

    // Then
    assertThat(file).extracting(SdqClient::codeName).isEqualTo("revised");
    assertThat(file).extracting(SdqClient::gender).isEqualTo(Gender.NON_BINARY);
    assertThat(file).extracting(SdqClient::ethnicity).isEqualTo(Ethnicity.ASIAN);
    assertThat(file)
        .extracting(SdqClient::disabilityTypes, list(DisabilityType.class))
        .containsExactlyInAnyOrder(
            DisabilityType.LEARNING,
            DisabilityType.COGNITIVE_OR_MEMORY,
            DisabilityType.MENTAL_HEALTH_CONDITION);
    assertThat(file)
        .extracting(SdqClient::interventions, list(Intervention.class))
        .satisfiesExactlyInAnyOrder(
            d -> {
              assertThat(d).extracting(Intervention::type).isEqualTo(InterventionType.CCPT);
              assertThat(d).extracting(Intervention::sessions).isEqualTo(3);
            },
            d -> {
              assertThat(d).extracting(Intervention::type).isEqualTo(InterventionType.PTP);
              assertThat(d).extracting(Intervention::sessions).isEqualTo(2);
            },
            d -> {
              assertThat(d).extracting(Intervention::type).isEqualTo(InterventionType.CPRT);
              assertThat(d).extracting(Intervention::sessions).isEqualTo(8);
            },
            d -> {
              assertThat(d).extracting(Intervention::type).isEqualTo(InterventionType.IA);
              assertThat(d).extracting(Intervention::sessions).isEqualTo(4);
            });
  }
}
