package uk.ratracejoe.sdq.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

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
import uk.ratracejoe.sdq.model.SdqClient;
import uk.ratracejoe.sdq.model.demographics.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ManageClientTest {
  @Autowired private SdqDatabaseInitializer sdqDatabaseInitializer;
  private SdqApi sdqApi;
  @LocalServerPort int port;

  @BeforeEach
  void beforeAll() {
    sdqDatabaseInitializer.resetAndMigrate();
    sdqApi = new SdqApi(port);
  }

  @Test
  void updateClient() {
    LocalDate dob = LocalDate.now().minusYears(20);
    SdqClient toCreate =
        SdqClient.builder()
            .codeName("Dave Lister")
            .dateOfBirth(dob)
            .council(Council.CHELTENHAM)
            .careExperience(CareExperience.YES_ADOPTED)
            .disabilityType(DisabilityType.LEARNING)
            .disabilityStatus(DisabilityStatus.DISABILITY)
            .fundingSource(FundingSource.EHCP)
            .interventionTypes(List.of(InterventionType.IA, InterventionType.CCPT))
            .aces(2)
            .build();
    SdqClient client = sdqApi.createClient(toCreate);
    assertThat(client)
        .extracting(SdqClient::interventionTypes, list(InterventionType.class))
        .containsExactlyInAnyOrder(InterventionType.IA, InterventionType.CCPT);

    SdqClient toUpdate =
        SdqClient.builder()
            .clientId(client.clientId())
            .codeName("Arnold Rimmer")
            .careExperience(CareExperience.KINSHIP)
            .disabilityType(DisabilityType.COGNITIVE_OR_MEMORY)
            .interventionTypes(List.of(InterventionType.IA, InterventionType.PTP))
            .build();
    SdqClient updated = sdqApi.updateClient(toUpdate);
    SdqClient afterUpdate = sdqApi.getClient(client.clientId());

    assertThat(updated).isEqualTo(afterUpdate);
    assertThat(updated).extracting(SdqClient::fundingSource).isEqualTo(FundingSource.EHCP);
    assertThat(updated).extracting(SdqClient::careExperience).isEqualTo(CareExperience.KINSHIP);
    assertThat(updated)
        .extracting(SdqClient::interventionTypes, list(InterventionType.class))
        .containsExactlyInAnyOrder(InterventionType.IA, InterventionType.PTP);
  }
}
