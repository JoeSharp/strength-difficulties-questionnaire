package uk.ratracejoe.sdq;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import uk.ratracejoe.sdq.dto.GoalQueryDTO;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.ParsedFile;
import uk.ratracejoe.sdq.model.ReportingPeriod;
import uk.ratracejoe.sdq.model.SdqClient;
import uk.ratracejoe.sdq.model.demographics.DemographicFilter;
import uk.ratracejoe.sdq.model.gbo.GboSubmission;
import uk.ratracejoe.sdq.model.gbo.Goal;
import uk.ratracejoe.sdq.model.gbo.GoalProgress;
import uk.ratracejoe.sdq.model.sdq.SdqSubmission;
import uk.ratracejoe.sdq.model.sdq.SdqSubmissionSummary;

@RequiredArgsConstructor
public class SdqApiClient {
  private static final String REST_URL_ADMIN = "/api/admin";
  private static final String REST_URL_UPLOAD = "/api/upload";
  private static final String REST_URL_CLIENT = "/api/client";
  private static final String REST_URL_SDQ = "/api/sdq";
  private static final String REST_URL_GOAL = "/api/goal";
  private final RestClient restClient;

  public SdqApiClient(int port) {
    this.restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
  }

  public ResponseEntity<Void> clearDatabase() {
    return restClient.delete().uri(REST_URL_ADMIN).retrieve().toBodilessEntity();
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

  public ResponseEntity<List<GoalProgress>> getGoalsWithProgress(GoalQueryDTO query) {
    return restClient
        .post()
        .uri(REST_URL_GOAL + "/query")
        .body(query)
        .contentType(MediaType.APPLICATION_JSON)
        .retrieve()
        .toEntity(new ParameterizedTypeReference<>() {});
  }

  public ResponseEntity<List<ReportingPeriod>> getSdqReportingPeriods(UUID clientId) {
    return restClient
        .get()
        .uri(REST_URL_SDQ + "/{clientId}/reportingPeriods", clientId)
        .retrieve()
        .toEntity(new ParameterizedTypeReference<>() {});
  }

  public ResponseEntity<SdqSubmission> getSdqSubmission(UUID periodId, Assessor assessor) {
    return restClient
        .get()
        .uri(REST_URL_SDQ + "/{periodId}/{assessor}", periodId, assessor)
        .retrieve()
        .toEntity(SdqSubmission.class);
  }

  public ResponseEntity<SdqSubmissionSummary> getSdqSubmissionSummary(
      UUID periodId, Assessor assessor) {
    return restClient
        .get()
        .uri(REST_URL_SDQ + "/{periodId}/{assessor}/summary", periodId, assessor)
        .retrieve()
        .toEntity(SdqSubmissionSummary.class);
  }

  public SdqClient createClient(SdqClient newClient) {
    return restClient
        .post()
        .uri(REST_URL_CLIENT)
        .body(newClient)
        .retrieve()
        .toEntity(SdqClient.class)
        .getBody();
  }

  public SdqClient getClient(UUID clientId) {
    return restClient
        .get()
        .uri(String.format("%s/%s", REST_URL_CLIENT, clientId))
        .retrieve()
        .toEntity(SdqClient.class)
        .getBody();
  }

  public SdqClient updateClient(SdqClient client) {
    return restClient
        .put()
        .uri(REST_URL_CLIENT)
        .body(client)
        .retrieve()
        .toEntity(SdqClient.class)
        .getBody();
  }

  public List<SdqClient> searchClients(List<DemographicFilter> filters) {
    return restClient
        .post()
        .uri(REST_URL_CLIENT + "/search")
        .body(filters)
        .retrieve()
        .toEntity(new ParameterizedTypeReference<List<SdqClient>>() {})
        .getBody();
  }

  public Goal createGoal(Goal goal) {
    var response = restClient.post().uri(REST_URL_GOAL).body(goal).retrieve().toEntity(Goal.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    return response.getBody();
  }

  public List<Goal> getGoalsForClient(UUID clientId) {
    return restClient
        .get()
        .uri(String.format("%s/forClient/%s", REST_URL_GOAL, clientId))
        .retrieve()
        .toEntity(new ParameterizedTypeReference<List<Goal>>() {})
        .getBody();
  }

  public Goal updateGoal(Goal goal) {
    return restClient.put().uri(REST_URL_GOAL).body(goal).retrieve().toEntity(Goal.class).getBody();
  }

  public Goal getGoal(UUID goalId) {
    return restClient
        .get()
        .uri(String.format("%s/%s", REST_URL_GOAL, goalId))
        .retrieve()
        .toEntity(Goal.class)
        .getBody();
  }

  public List<GboSubmission> getSubmissionsForGoal(UUID clientId, UUID goalId) {
    return restClient
        .get()
        .uri(String.format("%s/%s/scores/%s", REST_URL_GOAL, clientId, goalId))
        .retrieve()
        .toEntity(new ParameterizedTypeReference<List<GboSubmission>>() {})
        .getBody();
  }

  public void submitGbo(GboSubmission submission) {
    var response =
        restClient
            .post()
            .uri(REST_URL_GOAL + "/score")
            .body(submission)
            .retrieve()
            .toBodilessEntity();
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
