package uk.ratracejoe.sdq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ratracejoe.sdq.SdqFileParser;
import uk.ratracejoe.sdq.repository.*;
import uk.ratracejoe.sdq.service.*;

@Configuration
public class ServiceConfig {
  @Bean
  public ClientService clientFileService(ClientRepository repository) {
    return new ClientServiceImpl(repository);
  }

  @Bean
  public GboService gboService(GboRepository repository) {
    return new GboServiceImpl(repository);
  }

  @Bean
  public ReportingPeriodService reportingPeriodService(ReportingPeriodRepository repository) {
    return new ReportingPeriodServiceImpl(repository);
  }

  @Bean
  public RefDataService refDataService() {
    return new RefDataServiceImpl();
  }

  @Bean
  public SdqService sdqService(
      SdqRepository sdqRepository, ReportingPeriodRepository reportingPeriodRepository) {
    return new SdqServiceImpl(sdqRepository, reportingPeriodRepository);
  }

  @Bean
  public UploadService uploadService(
      InterventionTypeRepository interventionTypeRepository,
      ClientRepository fileRepository,
      ReportingPeriodRepository reportingPeriodRepository,
      SdqRepository sdqRepository,
      GboRepository gboRepository,
      SdqFileParser fileParser) {
    return new UploadServiceImpl(
        interventionTypeRepository,
        fileRepository,
        reportingPeriodRepository,
        sdqRepository,
        gboRepository,
        fileParser);
  }
}
