package uk.ratracejoe.sdq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.repository.*;

@Configuration
public class RepositoryConfig {

  @Bean
  public SdqClientRepository clientFileRepository(JdbcClient jdbcClient) {
    return new SdqClientRepositoryImpl(jdbcClient);
  }

  @Bean
  public DemographicOptionRepository demographicOptionRepository(JdbcClient jdbcClient) {
    return new DemographicOptionRepositoryImpl(jdbcClient);
  }

  @Bean
  public GboRepository gboRepository(JdbcClient jdbcClient) {
    return new GboRepositoryImpl(jdbcClient);
  }

  @Bean
  public InterventionTypeRepository interventionTypeRepository(JdbcClient jdbcClient) {
    return new InterventionTypeRepositoryImpl(jdbcClient);
  }

  @Bean
  public SdqRepository sdqRepository(JdbcClient jdbcClient) {
    return new SdqRepositoryImpl(jdbcClient);
  }
}
