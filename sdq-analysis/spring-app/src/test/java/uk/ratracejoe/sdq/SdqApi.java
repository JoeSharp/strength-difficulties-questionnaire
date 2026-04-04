package uk.ratracejoe.sdq;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import uk.ratracejoe.sdq.model.*;

@RequiredArgsConstructor
public class SdqApi {
  private static final String REST_URL_UPLOAD = "/api/upload";
  private static final String REST_URL_CLIENT = "/api/client";
  private static final String REST_URL_SDQ = "/api/sdq";
  private static final String REST_URL_GBO = "/api/gbo";
  private final RestClient restClient;

  public SdqApi(int port) {
    this.restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
  }

  public ResponseEntity<List<ParsedFile>> ingestFile(String... filenames) {
    var ingestFileRequest = getFilePost("sdqFiles", filenames);
    return restClient
        .post()
        .uri(REST_URL_UPLOAD)
        .body(ingestFileRequest)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .retrieve()
        .toEntity(new ParameterizedTypeReference<>() {});
  }

  public ResponseEntity<SdqClient> createClient(SdqClient newClient) {
    return restClient
        .post()
        .uri(REST_URL_CLIENT)
        .body(newClient)
        .retrieve()
        .toEntity(SdqClient.class);
  }

  public ResponseEntity<List<SdqClient>> searchClients(Map<DemographicField, String> filters) {
    return restClient
        .post()
        .uri(REST_URL_CLIENT + "/search")
        .body(filters)
        .retrieve()
        .toEntity(new ParameterizedTypeReference<>() {});
  }

  public void submitGbo(GboSubmission submission) {
    var response =
        restClient.post().uri(REST_URL_GBO).body(submission).retrieve().toBodilessEntity();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  public void submitSdq(SdqSubmission submission) {
    var response =
        restClient.post().uri(REST_URL_SDQ).body(submission).retrieve().toBodilessEntity();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  public static MultiValueMap<String, Object> getFilePost(String paramName, String... filenames) {
    // Load file from src/test/resources
    List<FileSystemResource> resources =
        Stream.of(filenames)
            .map(
                filename -> {
                  File file = null;
                  try {
                    file = new ClassPathResource(filename).getFile();
                  } catch (IOException e) {
                    throw new AssertionError("Could not load file " + filename, e);
                  }
                  return new FileSystemResource(file);
                })
            .toList();

    // Prepare multipart body
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.addAll(paramName, resources);
    return body;
  }
}
