package uk.ratracejoe.sdq.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import uk.ratracejoe.sdq.SdqApi;
import uk.ratracejoe.sdq.SdqDatabaseInitializer;
import uk.ratracejoe.sdq.model.gbo.GoalProgress;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GoalAnalyticTests {
  @Autowired private SdqDatabaseInitializer sdqDatabaseInitializer;

  @Autowired private GoalRepository goalRepository;
  private SdqApi sdqApi;

  @LocalServerPort int port;

  @BeforeEach
  void beforeEach() {
    sdqDatabaseInitializer.resetAndMigrate();
    sdqApi = new SdqApi(port);
    sdqApi.ingestFile(
        "Test File 1.xlsx",
        "Test File 4.xlsx",
        "Test File 5.xlsx",
        "Test File 6.xlsx",
        "Test File 7.xlsx",
        "Test File 8.xlsx",
        "Test File 9.xlsx",
        "Test File 10.xlsx");
  }

  @Test
  void getProgress() {
    List<GoalProgress> result =
        goalRepository.getGoalsWithProgress(3, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 10, 1));

    assertThat(result).hasSizeGreaterThan(1);
  }
}
