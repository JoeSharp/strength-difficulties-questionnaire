package uk.ratracejoe.sdq.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import uk.ratracejoe.sdq.SdqApiClient;
import uk.ratracejoe.sdq.SdqFixtures;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.ReportingPeriod;
import uk.ratracejoe.sdq.model.SdqClient;
import uk.ratracejoe.sdq.model.demographics.Council;
import uk.ratracejoe.sdq.model.demographics.DemographicField;
import uk.ratracejoe.sdq.model.demographics.DemographicFilter;
import uk.ratracejoe.sdq.model.demographics.Gender;
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
  void getFilteredFiles() {
    fixtures.givenAllTestFilesIngested();

    var fileResponse =
        client.searchClients(
            List.of(
                new DemographicFilter(DemographicField.Gender, List.of(Gender.MALE.name())),
                new DemographicFilter(
                    DemographicField.Council, List.of(Council.CHELTENHAM.name()))));
    assertThat(fileResponse).hasSizeGreaterThanOrEqualTo(1);
    assertThat(fileResponse).extracting(SdqClient::codeName).contains("Test File 4.xlsx");
  }

  @Test
  void bulkImportLifecycle() {
    var ingestResponse = client.ingestFile("Test File 1.xlsx");
    assertThat(ingestResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
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
