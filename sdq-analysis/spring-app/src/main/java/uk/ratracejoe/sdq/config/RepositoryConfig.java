package uk.ratracejoe.sdq.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ratracejoe.sdq.repository.*;

@Configuration
public class RepositoryConfig {

  @Bean
  public ClientFileRepository clientFileRepository(DataSource dataSource) {
    return new ClientFileRepositoryImpl(dataSource);
  }

  @Bean
  public DemographicOptionRepository demographicOptionRepository(DataSource dataSource) {
    return new DemographicOptionRepositoryImpl(dataSource);
  }

  @Bean
  public GboRepository gboRepository(DataSource dataSource) {
    return new GboRepositoryImpl(dataSource);
  }

  @Bean
  public InterventionTypeRepository interventionTypeRepository(DataSource dataSource) {
    return new InterventionTypeRepositoryImpl(dataSource);
  }

  @Bean
  public SdqRepository sdqRepository(DataSource dataSource) {
    return new SdqRepositoryImpl(dataSource);
  }
}
