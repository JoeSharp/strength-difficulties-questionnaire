package uk.ratracejoe.sdq.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import uk.ratracejoe.sdq.SdqApi;
import uk.ratracejoe.sdq.SdqDatabaseInitializer;
import uk.ratracejoe.sdq.model.*;
import uk.ratracejoe.sdq.utils.PeriodSupplier;

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
  void processGbos() {
    Instant dob = ZonedDateTime.now(ZoneId.systemDefault()).minusYears(20).toInstant();
    SdqClient toCreate =
        SdqClient.builder()
            .codeName("Dave Lister")
            .dateOfBirth(dob)
            .council(Council.CHELTENHAM)
            .careExperience(CareExperience.YES_ADOPTED)
            .disabilityType(DisabilityType.LEARNING)
            .disabilityStatus(DisabilityStatus.DISABILITY)
            .fundingSource(FundingSource.EHCP)
            .aces(2)
            .build();
    SdqClient created = sdqApi.createClient(toCreate).getBody();

    Supplier<Instant> tenDayPeriod = PeriodSupplier.periodicDays(10);
    Stream.of(10, 15, 25, 50)
            .map(score -> new GboScore(1, score))
            .map(score ->
              GboSubmission.builder()
                      .clientId(created.clientId())
                      .assessor(Assessor.School)
                      .period(tenDayPeriod.get())
                      .scores(List.of(score))
                      .build()
            )
                    .forEach(sdqApi::submitGbo);

    assertThat(created.codeName()).isEqualTo(toCreate.codeName());
  }
}
