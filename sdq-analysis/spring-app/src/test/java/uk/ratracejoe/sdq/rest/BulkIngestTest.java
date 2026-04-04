package uk.ratracejoe.sdq.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import uk.ratracejoe.sdq.SdqApi;
import uk.ratracejoe.sdq.SdqDatabaseInitializer;
import uk.ratracejoe.sdq.model.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BulkIngestTest {
  @Autowired private SdqDatabaseInitializer sdqDatabaseInitializer;

  private SdqApi sdqApi;

  @LocalServerPort int port;

  @BeforeEach
  void beforeAll() {
    sdqDatabaseInitializer.resetAndMigrate();
    sdqApi = new SdqApi(port);
  }

  @Test
  void getFilteredFiles() {
    ResponseEntity<List<ParsedFile>> created =
        sdqApi.ingestFile(
            "Test File 1.xlsx",
            "Test File 4.xlsx",
            "Test File 5.xlsx",
            "Test File 6.xlsx",
            "Test File 7.xlsx",
            "Test File 8.xlsx",
            "Test File 9.xlsx",
            "Test File 10.xlsx");
    assertThat(created.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(created.getBody()).hasSize(8);

    var fileResponse =
        sdqApi.searchClients(
            Map.of(
                DemographicField.Gender, Gender.MALE.name(),
                DemographicField.Council, Council.CHELTENHAM.name()));
    assertThat(fileResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(fileResponse.getBody()).hasSizeGreaterThanOrEqualTo(1);
    assertThat(fileResponse.getBody()).extracting(SdqClient::codeName).contains("Test File 4.xlsx");
  }

  @Test
  void bulkImportLifecycle() {
    var ingestResponse = sdqApi.ingestFile("Test File 1.xlsx");
    assertThat(ingestResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(ingestResponse.getBody()).hasSize(1);
    var clientUuid = ingestResponse.getBody().get(0).sdqClient().clientId();
  }
}
