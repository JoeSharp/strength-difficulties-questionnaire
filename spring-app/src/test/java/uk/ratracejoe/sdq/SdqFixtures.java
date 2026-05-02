package uk.ratracejoe.sdq;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SdqFixtures {
  private final SdqApiClient sdqClient;

  public SdqFixtures(int port) {
    sdqClient = new SdqApiClient(port);
    sdqClient.clearDatabase();
  }

  public void givenAllTestFilesIngested() {
    var response =
        sdqClient.ingestFile(
            "Test File 1.xlsx",
            "Test File 4.xlsx",
            "Test File 5.xlsx",
            "Test File 6.xlsx",
            "Test File 7.xlsx",
            "Test File 8.xlsx",
            "Test File 9.xlsx",
            "Test File 10.xlsx");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).hasSize(8);
  }
}
