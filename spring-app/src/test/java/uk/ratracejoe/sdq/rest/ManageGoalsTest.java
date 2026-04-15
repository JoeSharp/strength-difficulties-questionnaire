package uk.ratracejoe.sdq.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import uk.ratracejoe.sdq.SdqApi;
import uk.ratracejoe.sdq.model.*;
import uk.ratracejoe.sdq.model.demographics.*;
import uk.ratracejoe.sdq.model.gbo.GboSubmission;
import uk.ratracejoe.sdq.model.gbo.Goal;
import uk.ratracejoe.sdq.utils.PeriodSupplier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ManageGoalsTest {
  private SdqApi sdqApi;
  @LocalServerPort int port;

  @BeforeEach
  void beforeAll() {
    sdqApi = new SdqApi(port);
    sdqApi.clearDatabase();
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
            .disabilityType(DisabilityType.LEARNING)
            .disabilityStatus(DisabilityStatus.DISABILITY)
            .fundingSource(FundingSource.EHCP)
            .aces(2)
            .build();
    SdqClient client = sdqApi.createClient(toCreate);

    Goal goal =
        sdqApi.createGoal(
            Goal.builder().clientId(client.clientId()).description("Get exercise").build());

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
        .forEach(sdqApi::submitGbo);
    assertThat(client.codeName()).isEqualTo(toCreate.codeName());

    List<Goal> goals = sdqApi.getGoalsForClient(client.clientId());
    assertThat(goals)
        .satisfiesOnlyOnce(
            g -> {
              assertThat(g.clientId()).isEqualTo(client.clientId());
              assertThat(g.description()).isEqualTo(goal.description());
            });

    Goal updated =
        sdqApi.updateGoal(
            Goal.builder()
                .clientId(client.clientId())
                .goalId(goal.goalId())
                .description("Eat Chocolate")
                .build());
    Goal afterUpdate = sdqApi.getGoal(goal.goalId());
    assertThat(updated).isEqualTo(afterUpdate);
    assertThat(afterUpdate).extracting(Goal::description).isEqualTo("Eat Chocolate");
  }
}
