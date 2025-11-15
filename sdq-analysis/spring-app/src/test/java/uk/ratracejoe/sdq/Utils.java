package uk.ratracejoe.sdq;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.ratracejoe.sdq.xslx.XslxDemographicExtractor;

public class Utils {
  public static final String XLSX_TEST_FILE = "Test File 1.xlsx";

  public static InputStream workbookStream() {
    return XslxDemographicExtractor.class.getClassLoader().getResourceAsStream(XLSX_TEST_FILE);
  }

  public static HttpEntity<MultiValueMap<String, Object>> getFilePost(
      String paramName, String... filenames) {
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

    // Set headers
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    // Wrap in HttpEntity
    return new HttpEntity<>(body, headers);
  }
}
