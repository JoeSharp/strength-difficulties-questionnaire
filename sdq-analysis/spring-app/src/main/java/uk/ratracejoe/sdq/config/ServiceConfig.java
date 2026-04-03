package uk.ratracejoe.sdq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ratracejoe.sdq.SdqFileParser;
import uk.ratracejoe.sdq.repository.*;
import uk.ratracejoe.sdq.service.*;

@Configuration
public class ServiceConfig {
  @Bean
  public SdqClientService clientFileService(SdqClientRepository fileRepository) {
    return new SdqClientServiceImpl(fileRepository);
  }

  @Bean
  public GboService gboService(GboRepository gboRepository) {
    return new GboServiceImpl(gboRepository);
  }

  @Bean
  public RefDataService refDataService(DemographicOptionRepository demographicOptionRepository) {
    return new RefDataServiceImpl(demographicOptionRepository);
  }

  @Bean
  public SdqService sdqService(SdqRepository sdqRepository) {
    return new SdqServiceImpl(sdqRepository);
  }

  @Bean
  public UploadService uploadService(
      DemographicOptionRepository demographicOptionRepository,
      InterventionTypeRepository interventionTypeRepository,
      SdqClientRepository fileRepository,
      SdqService sdqService,
      GboService gboService,
      SdqFileParser fileParser) {
    return new UploadServiceImpl(
        demographicOptionRepository,
        interventionTypeRepository,
        fileRepository,
        sdqService,
        gboService,
        fileParser);
  }
}
