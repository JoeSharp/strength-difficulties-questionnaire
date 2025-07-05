package uk.ratracejoe.sdq_analysis.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import uk.ratracejoe.sdq_analysis.SdqTestExtension;
import uk.ratracejoe.sdq_analysis.dto.DatabaseStructure;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.ratracejoe.sdq_analysis.Utils.getWorkbookPost;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({SdqTestExtension.class})
class DatabaseControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    private static final String REST_URL_DATABASE = "/api/database";

    @Test
    void databaseDoesNotExistInitially() {
        var response = restTemplate.getForEntity(REST_URL_DATABASE, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void databaseCreationRegistersCorrectly() throws IOException {
        // Given
        var request = getWorkbookPost("sdqFile");

        // When
        var createResponse = restTemplate.postForEntity(REST_URL_DATABASE, request, DatabaseStructure.class);
        var existsResponse = restTemplate.getForEntity(REST_URL_DATABASE, Void.class);

        // Then
        assertThat(createResponse).isNotNull();
        assertThat(existsResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
