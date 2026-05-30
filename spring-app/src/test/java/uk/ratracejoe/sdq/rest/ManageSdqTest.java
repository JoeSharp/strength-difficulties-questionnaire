package uk.ratracejoe.sdq.rest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import uk.ratracejoe.sdq.SdqApiClient;
import uk.ratracejoe.sdq.SdqFixtures;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.ParsedFile;
import uk.ratracejoe.sdq.model.sdq.SdqProgressSummary;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ManageSdqTest {
  private SdqApiClient client;
  private SdqFixtures fixtures;
  @LocalServerPort int port;

  @BeforeEach
  void beforeAll() {
    fixtures = new SdqFixtures(port);
    client = fixtures.getSdqClient();
  }

  @Test
  void getSdqProgress() {
    // Given
    ParsedFile file = fixtures.fileIngested("Test File 4.xlsx");

    // When
    ResponseEntity<SdqProgressSummary> response =
        this.client.getSdqSubmissionProgress(file.sdqClient().clientId(), Assessor.School);

    // Then
    assertThat(response).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
  }
}
