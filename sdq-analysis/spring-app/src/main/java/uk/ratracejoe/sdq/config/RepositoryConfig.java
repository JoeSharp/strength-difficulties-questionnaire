package uk.ratracejoe.sdq.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ratracejoe.sdq.DatabaseServiceImpl;
import uk.ratracejoe.sdq.repository.*;

@Configuration
public class RepositoryConfig {

  @Bean
  public DatabaseService databaseService(DataSource dataSource, DbConfig dbConfig) {
    return new DatabaseServiceImpl(dataSource, dbConfig.getDatabaseFile());
  }

  @Bean
  public ClientFileRepository clientFileRepository(DataSource dataSource) {
    return new ClientFileRepository(dataSource);
  }

  @Bean
  public DemographicOptionRepository demographicOptionRepository(DataSource dataSource) {
    return new DemographicOptionRepository(dataSource);
  }

  @Bean
  public GboRepository gboRepository(DataSource dataSource) {
    return new GboRepository(dataSource);
  }

  @Bean
  public InterventionTypeRepository interventionTypeRepository(DataSource dataSource) {
    return new InterventionTypeRepository(dataSource);
  }

  @Bean
  public SdqRepository sdqRepository(DataSource dataSource) {
    return new SdqRepository(dataSource);
  }
}
