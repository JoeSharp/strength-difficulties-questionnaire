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
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.sdq.SdqProgress;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SdqAnalyticTests {
  @Autowired private SdqDatabaseInitializer sdqDatabaseInitializer;

  @Autowired private SdqRepository sdqRepository;

  @LocalServerPort int port;

  @BeforeEach
  void beforeEach() {
    sdqDatabaseInitializer.resetAndMigrate();
    SdqApi sdqApi = new SdqApi(port);
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
    List<SdqProgress> result =
        sdqRepository.getSdqProgress(
            Assessor.School, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 10, 1));

    assertThat(result).isNotNull();
  }
}
