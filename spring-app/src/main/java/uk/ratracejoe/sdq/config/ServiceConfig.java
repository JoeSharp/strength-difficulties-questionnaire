package uk.ratracejoe.sdq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ratracejoe.sdq.ClientFileParser;
import uk.ratracejoe.sdq.repository.*;
import uk.ratracejoe.sdq.service.*;

@Configuration
public class ServiceConfig {

  @Bean
  public AdminService adminService(
      ClientRepository clientRepository,
      SdqRepository sdqRepository,
      GboRepository gboRepository,
      GoalRepository goalRepository,
      InterventionRepository interventionRepository,
      DisabilityTypeRepository disabilityTypeRepository,
      ReportingPeriodRepository reportingPeriodRepository) {
    return new AdminServiceImpl(
        clientRepository,
        sdqRepository,
        gboRepository,
        goalRepository,
        interventionRepository,
        disabilityTypeRepository,
        reportingPeriodRepository);
  }

  @Bean
  public ClientService clientFileService(
      ClientRepository clientRepository,
      InterventionRepository interventionRepository,
      DisabilityTypeRepository disabilityTypeRepository) {
    return new ClientServiceImpl(
        clientRepository, interventionRepository, disabilityTypeRepository);
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
  public RefDataService refDataService(StatementRepository statementRepository) {
    return new RefDataServiceImpl(statementRepository);
  }

  @Bean
  public SdqService sdqService(
      SdqRepository sdqRepository, ReportingPeriodRepository reportingPeriodRepository) {
    return new SdqServiceImpl(sdqRepository, reportingPeriodRepository);
  }

  @Bean
  public UploadService uploadService(
      InterventionRepository interventionRepository,
      DisabilityTypeRepository disabilityTypeRepository,
      ClientRepository fileRepository,
      ReportingPeriodRepository reportingPeriodRepository,
      SdqRepository sdqRepository,
      GboRepository gboRepository,
      GoalRepository goalRepository,
      ClientFileParser fileParser) {
    return new UploadServiceImpl(
        interventionRepository,
        disabilityTypeRepository,
        fileRepository,
        reportingPeriodRepository,
        sdqRepository,
        goalRepository,
        gboRepository,
        fileParser);
  }
}
