package uk.ratracejoe.sdq;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.ratracejoe.sdq.repository.GboRepository;
import uk.ratracejoe.sdq.repository.InterventionTypeRepository;
import uk.ratracejoe.sdq.repository.SdqClientRepository;
import uk.ratracejoe.sdq.repository.SdqRepository;

@Component
@RequiredArgsConstructor
public class SdqDatabaseInitializer {
  private final SdqClientRepository sdqClientRepository;
  private final SdqRepository sdqRepository;
  private final GboRepository gboRepository;
  private final InterventionTypeRepository interventionTypeRepository;

  public void resetAndMigrate() {
    sdqClientRepository.deleteAll();
    sdqRepository.deleteAll();
    gboRepository.deleteAll();
    interventionTypeRepository.deleteAll();
  }
}
