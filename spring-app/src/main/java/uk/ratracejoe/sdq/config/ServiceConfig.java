package uk.ratracejoe.sdq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ratracejoe.sdq.ClientFileParser;
import uk.ratracejoe.sdq.repository.*;
import uk.ratracejoe.sdq.service.*;

@Configuration
public class ServiceConfig {
  @Bean
  public ClientService clientFileService(
      ClientRepository clientRepository, InterventionTypeRepository interventionTypeRepository) {
    return new ClientServiceImpl(clientRepository, interventionTypeRepository);
  }

  @Bean
  public GoalService gboService(GboRepository repository, GoalRepository goalRepository) {
    return new GoalServiceImpl(repository, goalRepository);
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
      GoalRepository goalRepository,
      ClientFileParser fileParser) {
    return new UploadServiceImpl(
        interventionTypeRepository,
        fileRepository,
        reportingPeriodRepository,
        sdqRepository,
        goalRepository,
        gboRepository,
        fileParser);
  }
}
