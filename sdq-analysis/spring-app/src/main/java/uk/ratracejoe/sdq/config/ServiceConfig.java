package uk.ratracejoe.sdq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import uk.ratracejoe.sdq.repository.ClientFileRepository;
import uk.ratracejoe.sdq.repository.DatabaseService;
import uk.ratracejoe.sdq.repository.GboRepository;
import uk.ratracejoe.sdq.repository.InterventionTypeRepository;
import uk.ratracejoe.sdq.repository.SdqRepository;
import uk.ratracejoe.sdq.service.*;

@Configuration
public class ServiceConfig {
  @Bean
  @Primary
  public ClientFileService clientFileService(
      DatabaseService dbService,
      ClientFileRepository fileRepository,
      InterventionTypeRepository interventionTypeRepository) {
    return new ClientFileServiceImpl(dbService, fileRepository, interventionTypeRepository);
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
