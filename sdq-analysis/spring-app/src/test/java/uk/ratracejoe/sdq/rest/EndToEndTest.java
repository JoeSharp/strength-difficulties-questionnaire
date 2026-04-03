package uk.ratracejoe.sdq.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.ratracejoe.sdq.Utils.getFilePost;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import uk.ratracejoe.sdq.config.DbConfig;
import uk.ratracejoe.sdq.model.*;

// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @EnableAutoConfiguration(
//    exclude = {org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class})
class EndToEndTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(EndToEndTest.class);
  @Autowired private TestRestTemplate restTemplate;
  private static final String REST_URL_UPLOAD = "/api/upload";
  private static final String REST_URL_CLIENT_FILE = "/api/client";
  private static final String REST_URL_CLIENT_SDQ = "/api/client/sdq/";
  private static final String REST_URL_CLIENT_GBO = "/api/client/gbo/";

  @BeforeEach
  void beforeEach(ExtensionContext context) throws Exception {
    createDatabase(context);
  }

  @AfterEach
  void afterEach(ExtensionContext context) throws Exception {}

  @DynamicPropertySource
  static void registerDynamicProperties(DynamicPropertyRegistry registry) {
    registry.add("database.url", postgres::getJdbcUrl);
    registry.add("database.username", () -> "test");
    registry.add("database.password", () -> "test");
  }

  @Container
  private static final PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:17")
          .withDatabaseName("sdq")
          .withUsername("test")
          .withPassword("test");

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

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
            new ParameterizedTypeReference<List<SdqClient>>() {});
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
    var clientUuid = ingestResponse.getBody().get(0).sdqClient().clientId();

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

  public static void createDatabase(ExtensionContext context) throws SQLException {
    ApplicationContext appContext = SpringExtension.getApplicationContext(context);
    DbConfig dbConfig = appContext.getBean(DbConfig.class);
    try {
      DataSource dataSource = dbConfig.dataSource();
      Connection connection = dataSource.getConnection();
      Database database =
          DatabaseFactory.getInstance()
              .findCorrectDatabaseImplementation(new JdbcConnection(connection));
      Liquibase liquibase =
          new Liquibase(
              "changelog/db.changelog-master.yaml",
              new DirectoryResourceAccessor(new File("../sdq-database/liquibase/")),
              database);

      liquibase.update(new Contexts(), new LabelExpression());

    } catch (Exception e) {
      LOGGER.error("Failed to run Liquibase migration", e);
      throw new SQLException(e);
    }
  }
}
