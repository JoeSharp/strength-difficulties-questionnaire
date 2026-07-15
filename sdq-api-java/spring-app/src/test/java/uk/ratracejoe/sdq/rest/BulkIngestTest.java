package uk.ratracejoe.sdq.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import uk.ratracejoe.sdq.SdqApiClient;
import uk.ratracejoe.sdq.SdqFixtures;
import uk.ratracejoe.sdq.dto.ClientQueryDTO;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.ParsedFile;
import uk.ratracejoe.sdq.model.ReportingPeriod;
import uk.ratracejoe.sdq.model.SdqClient;
import uk.ratracejoe.sdq.model.demographics.*;
import uk.ratracejoe.sdq.model.sdq.SdqSubmission;
import uk.ratracejoe.sdq.model.sdq.SdqSubmissionSummary;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BulkIngestTest {
  private SdqFixtures fixtures;
  private SdqApiClient client;

  @LocalServerPort int port;

  @BeforeEach
  void beforeAll() {
    fixtures = new SdqFixtures(port);
    client = fixtures.getSdqClient();
  }

  @Test
  void revisedSheet() {
    // When
    ParsedFile file = fixtures.fileIngested("Master Data Record for 09.05.26 Revised.xlsx");
    SdqClient sdqClient = client.getClient(file.sdqClient().clientId());

    // Then
    assertThat(sdqClient)
        .extracting(SdqClient::disabilityTypes, list(DisabilityType.class))
        .containsExactlyInAnyOrder(
            DisabilityType.LEARNING,
            DisabilityType.COGNITIVE_OR_MEMORY,
            DisabilityType.MENTAL_HEALTH_CONDITION);
    assertThat(sdqClient.aces())
        .containsAllEntriesOf(
            Map.of(
                AceType.GENERIC, 6,
                AceType.COMMUNITY, 4,
                AceType.SOCIO_ECONOMIC, 2,
                AceType.HEALTH, 8,
                AceType.BEREAVEMENT, 4,
                AceType.CHILD_WELFARE, 10));
    assertThat(sdqClient)
        .extracting(SdqClient::interventions, list(Intervention.class))
        .extracting(Intervention::type, Intervention::sessions)
        .containsExactlyInAnyOrder(
            tuple(InterventionType.CCPT, 3),
            tuple(InterventionType.PTP, 2),
            tuple(InterventionType.CPRT, 8),
            tuple(InterventionType.IA, 4));
  }

  @Test
  void getFilteredFiles() {
    fixtures.givenAllTestFilesIngested();

    var fileResponse =
        client.searchClients(
            ClientQueryDTO.builder()
                .partialName("4")
                .filters(
                    List.of(
                        new DemographicFilter(DemographicField.Gender, List.of(Gender.MALE.name())),
                        new DemographicFilter(
                            DemographicField.Council, List.of(Council.CHELTENHAM.name()))))
                .build());
    assertThat(fileResponse).hasSizeGreaterThanOrEqualTo(1);
    assertThat(fileResponse).extracting(SdqClient::codeName).contains("Test File 4.xlsx");
  }

  @Test
  void bulkImportLifecycle() {
    var ingestResponse = client.ingestFile("Test File 1.xlsx");
    assertThat(ingestResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(ingestResponse.getBody()).hasSize(1);
    SdqClient sdqClient = ingestResponse.getBody().getFirst().sdqClient();

    List<ReportingPeriod> periods =
        this.client.getSdqReportingPeriods(sdqClient.clientId()).getBody();
    assertThat(periods).hasSizeGreaterThan(1);
    ReportingPeriod period = periods.getFirst();

    SdqSubmission sdqSubmission =
        this.client.getSdqSubmission(period.periodId(), Assessor.Parent1).getBody();
    assertThat(sdqSubmission).isNotNull();

    SdqSubmissionSummary sdqSubmissionSummary =
        this.client.getSdqSubmissionSummary(period.periodId(), Assessor.Parent1).getBody();
    assertThat(sdqSubmissionSummary).isNotNull();
  }
}
