package uk.ratracejoe.sdq.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.ratracejoe.sdq.Utils.getWorkbookPost;

import java.io.IOException;
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
import uk.ratracejoe.sdq.dto.Assessor;
import uk.ratracejoe.sdq.dto.GboSummary;
import uk.ratracejoe.sdq.dto.ParsedFile;
import uk.ratracejoe.sdq.dto.SdqSummary;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({SdqTestExtension.class})
class EndToEndTest {
  @Autowired private TestRestTemplate restTemplate;
  private static final String REST_URL_UPLOAD = "/api/upload";
  private static final String REST_URL_CLIENT_SDQ = "/api/client/sdq/";
  private static final String REST_URL_CLIENT_GBO = "/api/client/gbo/";

  @Test
  void completeLifecycle() throws IOException {
    int NUMBER_ASSESSORS = 4;
    int NUMBER_PERIODS = 9;

    var ingestFileRequest = getWorkbookPost("sdqFiles");
    var ingestResponse =
        restTemplate.exchange(
            REST_URL_UPLOAD,
            HttpMethod.POST,
            ingestFileRequest,
            new ParameterizedTypeReference<List<ParsedFile>>() {});
    assertThat(ingestResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(ingestResponse.getBody()).hasSize(1);
    var clientUuid = ingestResponse.getBody().get(0).clientFile().uuid();

    var sdqResponse =
        restTemplate.exchange(
            REST_URL_CLIENT_SDQ + clientUuid,
            HttpMethod.GET,
            new HttpEntity<>(null, new HttpHeaders()),
            new ParameterizedTypeReference<Map<Assessor, List<SdqSummary>>>() {});
    assertThat(sdqResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(sdqResponse.getBody()).hasSize(NUMBER_ASSESSORS);

    var gboResponse =
        restTemplate.exchange(
            REST_URL_CLIENT_GBO + clientUuid,
            HttpMethod.GET,
            new HttpEntity<>(null, new HttpHeaders()),
            new ParameterizedTypeReference<Map<Assessor, List<GboSummary>>>() {});
    assertThat(gboResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(gboResponse.getBody()).hasSize(NUMBER_ASSESSORS);
  }
}
