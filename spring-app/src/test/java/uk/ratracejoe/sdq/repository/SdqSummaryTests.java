package uk.ratracejoe.sdq.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import uk.ratracejoe.sdq.SdqApiClient;
import uk.ratracejoe.sdq.SdqFixtures;
import uk.ratracejoe.sdq.dto.GoalQueryDTO;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.ParsedFile;
import uk.ratracejoe.sdq.model.ReportingPeriod;
import uk.ratracejoe.sdq.model.demographics.DemographicField;
import uk.ratracejoe.sdq.model.demographics.DemographicFilter;
import uk.ratracejoe.sdq.model.demographics.Gender;
import uk.ratracejoe.sdq.model.sdq.Posture;
import uk.ratracejoe.sdq.model.sdq.SdqSubmissionSummary;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SdqSummaryTests {
  private SdqFixtures fixtures;
  private SdqApiClient client;

  @LocalServerPort int port;

  @BeforeEach
  void beforeEach() {
    fixtures = new SdqFixtures(port);
    client = fixtures.getSdqClient();
  }

  /** Still a WIP */
  @Disabled
  @Test
  void getSdqWithProgress() {
    fixtures.givenAllTestFilesIngested();

    List<SdqSubmissionSummary> summaries =
        client
            .getSdqWithProgress(
                GoalQueryDTO.builder()
                    .assessor(Assessor.School)
                    .filters(
                        List.of(
                            new DemographicFilter(
                                DemographicField.Gender, List.of(Gender.MALE.name()))))
                    .minProgress(3)
                    .from(LocalDate.of(2024, 5, 1))
                    .to(LocalDate.of(2025, 11, 1))
                    .build())
            .getBody();

    assertThat(summaries).isNotEmpty();
  }

  @Test
  void getSdqSummary() {
    // Given
    ResponseEntity<List<ParsedFile>> response = client.ingestFile("Test File 1.xlsx");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).hasSize(1);
    ParsedFile testFile1 = response.getBody().getFirst();

    // When
    ResponseEntity<List<ReportingPeriod>> reportingPeriods =
        client.getSdqReportingPeriods(testFile1.sdqClient().clientId());
    assertThat(reportingPeriods.getBody()).hasSize(2);
    assertThat(reportingPeriods.getBody())
        .extracting(ReportingPeriod::period)
        .satisfiesExactlyInAnyOrder(
            d -> assertThat(d).isEqualTo(LocalDate.of(2025, 8, 16)),
            d -> assertThat(d).isEqualTo(LocalDate.of(2025, 9, 17)));

    ReportingPeriod firstPeriod =
        reportingPeriods.getBody().stream()
            .filter(p -> p.period().equals(LocalDate.of(2025, 8, 16)))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Couldn't find period"));
    ResponseEntity<SdqSubmissionSummary> firstSchoolSubmission =
        client.getSdqSubmissionSummary(firstPeriod.periodId(), Assessor.School);
    assertThat(firstSchoolSubmission.getBody())
        .satisfies(
            s -> {
              assertThat(s.categorySubTotals())
                  .containsEntry("Emotional", 7)
                  .containsEntry("Peer", 3)
                  .containsEntry("Conduct", 6)
                  .containsEntry("HyperActivity", 8)
                  .containsEntry("ProSocial", 6);
              assertThat(s.postureSubTotals())
                  .containsEntry(Posture.Internalising, 10)
                  .containsEntry(Posture.Externalising, 14);
              assertThat(s.totalDifficulties()).isEqualTo(24);
            });
  }
}
