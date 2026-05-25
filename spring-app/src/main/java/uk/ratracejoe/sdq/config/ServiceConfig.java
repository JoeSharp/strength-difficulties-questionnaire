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
      ReportingPeriodRepository reportingPeriodRepository) {
    return new AdminServiceImpl(
        clientRepository, sdqRepository, gboRepository, goalRepository, reportingPeriodRepository);
  }

  @Bean
  public ClientService clientFileService(ClientRepository clientRepository) {
    return new ClientServiceImpl(clientRepository);
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
      ClientRepository fileRepository,
      ReportingPeriodRepository reportingPeriodRepository,
      SdqRepository sdqRepository,
      GboRepository gboRepository,
      GoalRepository goalRepository,
      ClientFileParser fileParser) {
    return new UploadServiceImpl(
        fileRepository,
        reportingPeriodRepository,
        sdqRepository,
        goalRepository,
        gboRepository,
        fileParser);
  }
}
