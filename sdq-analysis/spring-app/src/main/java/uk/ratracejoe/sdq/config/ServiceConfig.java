package uk.ratracejoe.sdq.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import uk.ratracejoe.sdq.database.repository.ClientFileRepository;
import uk.ratracejoe.sdq.database.repository.GboRepository;
import uk.ratracejoe.sdq.database.repository.InterventionTypeRepository;
import uk.ratracejoe.sdq.database.repository.SdqRepository;
import uk.ratracejoe.sdq.repository.DatabaseService;
import uk.ratracejoe.sdq.service.*;

@Configuration
public class ServiceConfig {
  @Bean
  @Primary
  public ClientFileService clientFileService(
      DatabaseServiceImpl dbService,
      ClientFileRepository fileRepository,
      InterventionTypeRepository interventionTypeRepository) {
    return new ClientFileServiceImpl(dbService, fileRepository, interventionTypeRepository);
  }

  @Bean
  @Primary
  public DatabaseService databaseService(DataSource dataSource, DbConfig dbConfig) {
    return new DatabaseServiceImpl(dataSource, dbConfig);
  }

  @Bean
  @Primary
  public GboService gboService(DatabaseService dbService, GboRepository gboRepository) {
    return new GboServiceImpl(dbService, gboRepository);
  }

  @Bean
  @Primary
  public SdqService sdqService(DatabaseService databaseService, SdqRepository sdqRepository) {
    return new SdqServiceImpl(databaseService, sdqRepository);
  }
}
