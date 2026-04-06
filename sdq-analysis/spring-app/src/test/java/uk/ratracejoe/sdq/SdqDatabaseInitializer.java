package uk.ratracejoe.sdq;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.ratracejoe.sdq.repository.*;

@Component
@RequiredArgsConstructor
public class SdqDatabaseInitializer {
  private final ClientRepository clientRepository;
  private final SdqRepository sdqRepository;
  private final GboRepository gboRepository;
  private final GoalRepository goalRepository;
  private final InterventionTypeRepository interventionTypeRepository;
  private final ReportingPeriodRepository reportingPeriodRepository;

  public void resetAndMigrate() {
    clientRepository.deleteAll();
    goalRepository.deleteAll();
    sdqRepository.deleteAll();
    gboRepository.deleteAll();
    interventionTypeRepository.deleteAll();
    reportingPeriodRepository.deleteAll();
  }
}
