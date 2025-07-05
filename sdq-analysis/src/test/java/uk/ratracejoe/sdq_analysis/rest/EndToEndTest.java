package uk.ratracejoe.sdq_analysis.rest;

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
import uk.ratracejoe.sdq_analysis.SdqTestExtension;
import uk.ratracejoe.sdq_analysis.dto.DatabaseStructure;
import uk.ratracejoe.sdq_analysis.dto.ParsedFile;
import uk.ratracejoe.sdq_analysis.dto.SdqScoresSummary;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.ratracejoe.sdq_analysis.Utils.getWorkbookPost;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({SdqTestExtension.class})
class EndToEndTest {
    @Autowired
    private TestRestTemplate restTemplate;
    private static final String REST_URL_DATABASE = "/api/database";
    private static final String REST_URL_UPLOAD = "/api/upload";
    private static final String REST_URL_CLIENT_SCORES = "/api/client/scores/";

    @Test
    void completeLifecycle() throws IOException {
        int NUMBER_ASSESSORS = 4;
        int NUMBER_PERIODS = 9;
        var createDbRequest = getWorkbookPost("sdqFile" );
        var createResponse = restTemplate.postForEntity(REST_URL_DATABASE,
                createDbRequest,
                DatabaseStructure.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        var ingestFileRequest = getWorkbookPost("sdqFiles");
        var ingestResponse = restTemplate.exchange(REST_URL_UPLOAD, HttpMethod.POST, ingestFileRequest,
                new ParameterizedTypeReference<List<ParsedFile>>() {} );
        assertThat(ingestResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(ingestResponse.getBody()).hasSize(1);
        var clientUuid = ingestResponse.getBody().get(0).clientFile().uuid();

        var scoresResponse = restTemplate.exchange(REST_URL_CLIENT_SCORES + clientUuid,
                HttpMethod.GET, new HttpEntity<>(null, new HttpHeaders()), new ParameterizedTypeReference<List<SdqScoresSummary>>() {
                });
        assertThat(scoresResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(scoresResponse.getBody()).hasSize(NUMBER_ASSESSORS * NUMBER_PERIODS);
    }
}
