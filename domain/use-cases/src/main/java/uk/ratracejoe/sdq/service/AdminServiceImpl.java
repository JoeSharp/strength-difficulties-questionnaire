package uk.ratracejoe.sdq.service;

import lombok.RequiredArgsConstructor;
import uk.ratracejoe.sdq.repository.*;

@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
  private final ClientRepository clientRepository;
  private final SdqRepository sdqRepository;
  private final GboRepository gboRepository;
  private final GoalRepository goalRepository;
  private final InterventionRepository interventionRepository;
  private final DisabilityTypeRepository disabilityTypeRepository;
  private final ReportingPeriodRepository reportingPeriodRepository;
  private final AcesRepository acesRepository;

  @Override
  public void clearDatabase() {
    clientRepository.deleteAll();
    goalRepository.deleteAll();
    sdqRepository.deleteAll();
    gboRepository.deleteAll();
    interventionRepository.deleteAll();
    disabilityTypeRepository.deleteAll();
    reportingPeriodRepository.deleteAll();
    acesRepository.deleteAll();
  }
}
