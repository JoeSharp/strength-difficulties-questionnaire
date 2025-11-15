package uk.ratracejoe.sdq.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.ratracejoe.sdq.Utils.getFilePost;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import uk.ratracejoe.sdq.SdqTestExtension;
import uk.ratracejoe.sdq.model.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({SdqTestExtension.class})
class EndToEndTest {
  @Autowired private TestRestTemplate restTemplate;
  private static final String REST_URL_UPLOAD = "/api/upload";
  private static final String REST_URL_CLIENT_FILE = "/api/client";
  private static final String REST_URL_CLIENT_SDQ = "/api/client/sdq/";
  private static final String REST_URL_CLIENT_GBO = "/api/client/gbo/";

  @Test
  void getFilteredFiles() {
    var ingestFileRequest =
        getFilePost(
            "sdqFiles",
            "Test File 1.xlsx",
            "Test File 4.xlsx",
            "Test File 5.xlsx",
            "Test File 6.xlsx",
            "Test File 7.xlsx",
            "Test File 8.xlsx",
            "Test File 9.xlsx",
            "Test File 10.xlsx");
    restTemplate.exchange(
        REST_URL_UPLOAD,
        HttpMethod.POST,
        ingestFileRequest,
        new ParameterizedTypeReference<List<ParsedFile>>() {});
    Map<DemographicField, String> filters =
        Map.of(
            DemographicField.Gender, "Male",
            DemographicField.Council, "Cheltenham");

    var fileResponse =
        restTemplate.exchange(
            REST_URL_CLIENT_FILE,
            HttpMethod.POST,
            new HttpEntity<>(filters, new HttpHeaders()),
            new ParameterizedTypeReference<List<ClientFile>>() {});
    assertThat(fileResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(fileResponse.getBody()).hasSize(1);
  }

  @Test
  void completeLifecycle() {
    var ingestFileRequest = getFilePost("sdqFiles", "Test File 1.xlsx");
    var ingestResponse =
        restTemplate.exchange(
            REST_URL_UPLOAD,
            HttpMethod.POST,
            ingestFileRequest,
            new ParameterizedTypeReference<List<ParsedFile>>() {});
    assertThat(ingestResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(ingestResponse.getBody()).hasSize(1);
    var clientUuid = ingestResponse.getBody().get(0).clientFile().fileId();

    var sdqResponse =
        restTemplate.exchange(
            REST_URL_CLIENT_SDQ + clientUuid,
            HttpMethod.GET,
            new HttpEntity<>(null, new HttpHeaders()),
            new ParameterizedTypeReference<Map<Assessor, List<SdqScore>>>() {});
    assertThat(sdqResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(sdqResponse.getBody()).hasSize(4);

    var gboResponse =
        restTemplate.exchange(
            REST_URL_CLIENT_GBO + clientUuid,
            HttpMethod.GET,
            new HttpEntity<>(null, new HttpHeaders()),
            new ParameterizedTypeReference<Map<Assessor, List<GboScore>>>() {});
    assertThat(gboResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(gboResponse.getBody()).hasSize(4);
  }
}
