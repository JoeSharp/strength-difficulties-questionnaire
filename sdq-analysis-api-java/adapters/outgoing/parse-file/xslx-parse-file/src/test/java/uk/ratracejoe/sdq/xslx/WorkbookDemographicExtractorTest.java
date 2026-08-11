package uk.ratracejoe.sdq.xslx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.InstanceOfAssertFactories.list;
import static org.assertj.core.api.InstanceOfAssertFactories.map;
import static uk.ratracejoe.sdq.xslx.Utils.*;

import java.io.IOException;
import java.util.Map;
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
    assertThat(file)
        .extracting(SdqClient::aces, map(AceType.class, Integer.class))
        .containsExactly(Map.entry(AceType.GENERIC, 3));
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
    assertThat(file.aces())
        .containsAllEntriesOf(
            Map.of(
                AceType.GENERIC, 6,
                AceType.COMMUNITY, 4,
                AceType.SOCIO_ECONOMIC, 2,
                AceType.HEALTH, 8,
                AceType.BEREAVEMENT, 4,
                AceType.CHILD_WELFARE, 10));
    assertThat(file)
        .extracting(SdqClient::interventions, list(Intervention.class))
        .extracting(Intervention::type, Intervention::sessions)
        .containsExactlyInAnyOrder(
            tuple(InterventionType.CCPT, 3),
            tuple(InterventionType.PTP, 2),
            tuple(InterventionType.CPRT, 8),
            tuple(InterventionType.IA, 4));
  }
}
