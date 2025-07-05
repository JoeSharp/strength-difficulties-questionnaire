package uk.ratracejoe.sdq_analysis;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.ratracejoe.sdq_analysis.service.xslx.XslxDemographicExtractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Utils {
    public static final String XLSX_TEST_FILE = "MasterDataRecordFor28_6.xlsx";

    public static InputStream workbookStream() {
        return XslxDemographicExtractor.class.getClassLoader().getResourceAsStream(XLSX_TEST_FILE);
    }

    public static Workbook workbookLoaded() throws IOException {
        var input = workbookStream();
        assertNotNull(input); // Always good to check!
        return new XSSFWorkbook(input);
    }

    public static HttpEntity<MultiValueMap<String, Object>> getWorkbookPost(String paramName) throws IOException {
        return getFilePost(XLSX_TEST_FILE, paramName);
    }

    public static HttpEntity<MultiValueMap<String, Object>> getFilePost(String filename, String paramName) throws IOException {
        // Load file from src/test/resources
        File file = new ClassPathResource(filename).getFile();
        FileSystemResource resource = new FileSystemResource(file);

        // Prepare multipart body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add(paramName, resource);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Wrap in HttpEntity
        return new HttpEntity<>(body, headers);
    }
}
