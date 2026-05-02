package uk.ratracejoe.sdq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.repository.*;

@Configuration
public class RepositoryConfig {

  @Bean
  public ClientRepository clientFileRepository(JdbcClient jdbcClient) {
    return new ClientRepositoryImpl(jdbcClient);
  }

  @Bean
  public StatementRepository statementRepository(JdbcClient jdbcClient) {
    return new StatementRepositoryImpl(jdbcClient);
  }

  @Bean
  public GboRepository gboRepository(JdbcClient jdbcClient) {
    return new GboRepositoryImpl(jdbcClient);
  }

  @Bean
  public GoalRepository goalRepository(JdbcClient jdbcClient) {
    return new GoalRepositoryImpl(jdbcClient);
  }

  @Bean
  public InterventionTypeRepository interventionTypeRepository(JdbcClient jdbcClient) {
    return new InterventionTypeRepositoryImpl(jdbcClient);
  }

  @Bean
  public SdqRepository sdqRepository(
      JdbcClient jdbcClient, StatementRepository statementRepository) {
    return new SdqRepositoryImpl(jdbcClient, statementRepository);
  }

  @Bean
  public ReportingPeriodRepository reportingPeriodRepository(JdbcClient jdbcClient) {
    return new ReportingPeriodRepositoryImpl(jdbcClient);
  }
}
