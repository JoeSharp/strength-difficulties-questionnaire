package uk.ratracejoe.sdq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ratracejoe.sdq.exception.SdqExceptionHandler;
import uk.ratracejoe.sdq.rest.*;
import uk.ratracejoe.sdq.service.*;

@Configuration
public class RestConfig {
  @Bean
  public ClientController clientController(ClientService service) {
    return new ClientController(service);
  }

  @Bean
  public SdqController sdqController(SdqService service) {
    return new SdqController(service);
  }

  @Bean
  public GboController gboController(GboService service) {
    return new GboController(service);
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
