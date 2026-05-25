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
      ReportingPeriodRepository reportingPeriodRepository,
      AcesRepository acesRepository) {
    return new AdminServiceImpl(
        clientRepository,
        sdqRepository,
        gboRepository,
        goalRepository,
        interventionRepository,
        disabilityTypeRepository,
        reportingPeriodRepository,
        acesRepository);
  }

  @Bean
  public ClientService clientFileService(
      ClientRepository clientRepository,
      InterventionRepository interventionRepository,
      DisabilityTypeRepository disabilityTypeRepository,
      AcesRepository acesRepository) {
    return new ClientServiceImpl(
        clientRepository, interventionRepository, disabilityTypeRepository, acesRepository);
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
      AcesRepository acesRepository,
      InterventionRepository interventionRepository,
      DisabilityTypeRepository disabilityTypeRepository,
      ClientRepository fileRepository,
      ReportingPeriodRepository reportingPeriodRepository,
      SdqRepository sdqRepository,
      GboRepository gboRepository,
      GoalRepository goalRepository,
      ClientFileParser fileParser) {
    return new UploadServiceImpl(
        acesRepository,
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
