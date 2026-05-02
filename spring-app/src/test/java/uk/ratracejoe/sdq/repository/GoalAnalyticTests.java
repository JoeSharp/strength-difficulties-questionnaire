package uk.ratracejoe.sdq.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import uk.ratracejoe.sdq.SdqApiClient;
import uk.ratracejoe.sdq.SdqFixtures;
import uk.ratracejoe.sdq.dto.GoalQueryDTO;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.demographics.DemographicField;
import uk.ratracejoe.sdq.model.demographics.DemographicFilter;
import uk.ratracejoe.sdq.model.demographics.Gender;
import uk.ratracejoe.sdq.model.gbo.GoalProgress;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GoalAnalyticTests {

  private SdqFixtures fixtures;
  private SdqApiClient client;

  @LocalServerPort int port;

  @BeforeEach
  void beforeEach() {
    fixtures = new SdqFixtures(port);
    client = fixtures.getSdqClient();
  }

  @Test
  void getGoalsWithProgress() {
    fixtures.givenAllTestFilesIngested();

    List<GoalProgress> result =
        client
            .getGoalsWithProgress(
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
    assertThat(result).hasSize(1);
  }
}
