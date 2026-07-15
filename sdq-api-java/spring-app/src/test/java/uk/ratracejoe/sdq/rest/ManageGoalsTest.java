package uk.ratracejoe.sdq.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import uk.ratracejoe.sdq.SdqApiClient;
import uk.ratracejoe.sdq.SdqFixtures;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.ParsedFile;
import uk.ratracejoe.sdq.model.SdqClient;
import uk.ratracejoe.sdq.model.demographics.*;
import uk.ratracejoe.sdq.model.gbo.GboSubmission;
import uk.ratracejoe.sdq.model.gbo.Goal;
import uk.ratracejoe.sdq.model.gbo.GoalProgress;
import uk.ratracejoe.sdq.utils.PeriodSupplier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ManageGoalsTest {
  private SdqApiClient client;
  private SdqFixtures fixtures;
  @LocalServerPort int port;

  @BeforeEach
  void beforeAll() {
    fixtures = new SdqFixtures(port);
    client = fixtures.getSdqClient();
    client.clearDatabase();
  }

  @Test
  void getGoalsProgressForClient() {
    // Given
    ParsedFile file = fixtures.fileIngested("Test File 4.xlsx");

    // When
    List<GoalProgress> goals =
        this.client.getGoalsProgressForClient(file.sdqClient().clientId(), Assessor.School);

    // Then
    assertThat(goals).hasSizeGreaterThan(2);
  }

  @Test
  void getGoalProgress() {
    // Given
    ParsedFile file = fixtures.fileIngested("Test File 4.xlsx");

    // When
    Goal firstGoal = this.client.getGoalsForClient(file.sdqClient().clientId()).getFirst();
    GoalProgress progress = this.client.getGoalProgress(firstGoal.goalId(), Assessor.School);

    // Then
    assertThat(progress).extracting(GoalProgress::goal).isEqualTo(firstGoal);
  }

  @Test
  void buildGoals() {
    LocalDate dob = LocalDate.now().minusYears(20);
    SdqClient toCreate =
        SdqClient.builder()
            .codeName("Dave Lister")
            .dateOfBirth(dob)
            .council(Council.CHELTENHAM)
            .careExperience(CareExperience.YES_ADOPTED)
            .disabilityTypes(List.of(DisabilityType.LEARNING))
            .disabilityStatus(DisabilityStatus.DISABILITY)
            .fundingSource(FundingSource.EHCP)
            .aces(Map.of(AceType.GENERIC, 2))
            .build();
    SdqClient sdqClient = this.client.createClient(toCreate);

    Goal goal =
        this.client.createGoal(
            Goal.builder().clientId(sdqClient.clientId()).description("Get exercise").build());

    Supplier<LocalDate> tenDayPeriod = PeriodSupplier.periodicDays(10);
    Stream.of(10, 15, 25, 50)
        .map(
            score ->
                GboSubmission.builder()
                    .goalId(goal.goalId())
                    .assessor(Assessor.School)
                    .period(tenDayPeriod.get())
                    .score(score)
                    .build())
        .forEach(this.client::submitGbo);
    assertThat(sdqClient.codeName()).isEqualTo(toCreate.codeName());

    List<Goal> goals = this.client.getGoalsForClient(sdqClient.clientId());
    assertThat(goals)
        .satisfiesOnlyOnce(
            g -> {
              assertThat(g.clientId()).isEqualTo(sdqClient.clientId());
              assertThat(g.description()).isEqualTo(goal.description());
            });

    Goal updated =
        this.client.updateGoal(
            Goal.builder()
                .clientId(sdqClient.clientId())
                .goalId(goal.goalId())
                .description("Eat Chocolate")
                .build());
    Goal afterUpdate = this.client.getGoal(goal.goalId());
    assertThat(updated).isEqualTo(afterUpdate);
    assertThat(afterUpdate).extracting(Goal::description).isEqualTo("Eat Chocolate");
  }
}
