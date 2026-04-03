package uk.ratracejoe.sdq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ratracejoe.sdq.exception.SdqExceptionHandler;
import uk.ratracejoe.sdq.rest.ReferenceController;
import uk.ratracejoe.sdq.rest.SdqClientController;
import uk.ratracejoe.sdq.rest.UploadController;
import uk.ratracejoe.sdq.service.*;

@Configuration
public class RestConfig {
  @Bean
  public SdqClientController sdqClientController(
      SdqClientService fileService, SdqService sdqService, GboService gboService) {
    return new SdqClientController(fileService, sdqService, gboService);
  }

  @Bean
  public ReferenceController referenceController(RefDataService refDataService) {
    return new ReferenceController(refDataService);
  }

  @Bean
  public UploadController uploadController(UploadService uploadService) {
    return new UploadController(uploadService);
  }

  @Bean
  public SdqExceptionHandler sdqExceptionHandler() {
    return new SdqExceptionHandler();
  }
}
