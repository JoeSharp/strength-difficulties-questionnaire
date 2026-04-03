package uk.ratracejoe.sdq.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import uk.ratracejoe.sdq.SdqApi;
import uk.ratracejoe.sdq.SdqDatabaseInitializer;
import uk.ratracejoe.sdq.model.SdqClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ManageClientTest {
  @Autowired private SdqDatabaseInitializer sdqDatabaseInitializer;
  private SdqApi sdqApi;
  @LocalServerPort int port;

  @BeforeEach
  void beforeAll() {
    sdqDatabaseInitializer.resetAndMigrate();
    sdqApi = new SdqApi(port);
  }

  @Test
  void createFile() {
    Instant dob = ZonedDateTime.now(ZoneId.systemDefault()).minusYears(20).toInstant();
    SdqClient toCreate =
        SdqClient.builder()
            .codeName("Dave Lister")
            .dateOfBirth(dob)
            .council("Elrond")
            .careExperience("Yep")
            .disabilityType("Disabled")
            .disabilityStatus("Yes")
            .aces(2)
            .build();
    SdqClient created = sdqApi.createClient(toCreate).getBody();

    assertThat(created.codeName()).isEqualTo(toCreate.codeName());
  }
}
