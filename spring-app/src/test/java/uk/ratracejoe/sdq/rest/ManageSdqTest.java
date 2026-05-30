package uk.ratracejoe.sdq.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.map;

import java.util.Map;
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
import uk.ratracejoe.sdq.model.sdq.Posture;
import uk.ratracejoe.sdq.model.sdq.Progress;
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

  @Test
  void getSdqProgressFile7() {
    // Given
    ParsedFile file = fixtures.fileIngested("Test File 7.xlsx");

    // When
    ResponseEntity<SdqProgressSummary> response =
        this.client.getSdqSubmissionProgress(file.sdqClient().clientId(), Assessor.School);

    // Then
    assertThat(response).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
    assertThat(response)
        .extracting(ResponseEntity::getBody)
        .extracting(SdqProgressSummary::assessor)
        .isEqualTo(Assessor.School);
    assertThat(response).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
    assertThat(response)
        .extracting(ResponseEntity::getBody)
        .extracting(SdqProgressSummary::categoryProgress, map(String.class, Progress.class))
        .containsOnly(
            Map.entry("Emotional", Progress.builder().first(6).last(4).delta(-2).build()),
            Map.entry("Peer", Progress.builder().first(3).last(4).delta(1).build()),
            Map.entry("Conduct", Progress.builder().first(6).last(3).delta(-3).build()),
            Map.entry("HyperActivity", Progress.builder().first(6).last(4).delta(-2).build()),
            Map.entry("ProSocial", Progress.builder().first(4).last(7).delta(3).build()));
    assertThat(response)
        .extracting(ResponseEntity::getBody)
        .extracting(SdqProgressSummary::postureProgress, map(Posture.class, Progress.class))
        .containsOnly(
            Map.entry(Posture.Internalising, Progress.builder().first(9).last(8).delta(-1).build()),
            Map.entry(
                Posture.Externalising, Progress.builder().first(12).last(7).delta(-5).build()),
            Map.entry(Posture.ProSocial, Progress.builder().first(4).last(7).delta(3).build()));
    assertThat(response)
        .extracting(ResponseEntity::getBody)
        .extracting(SdqProgressSummary::totalDifficulties)
        .isEqualTo(Progress.builder().first(21).last(15).delta(-6).build());
  }
}
