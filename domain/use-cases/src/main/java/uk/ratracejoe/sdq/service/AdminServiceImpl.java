package uk.ratracejoe.sdq.service;

import lombok.RequiredArgsConstructor;
import uk.ratracejoe.sdq.repository.*;

@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
  private final ClientRepository clientRepository;
  private final SdqRepository sdqRepository;
  private final GboRepository gboRepository;
  private final GoalRepository goalRepository;
  private final ReportingPeriodRepository reportingPeriodRepository;

  @Override
  public void clearDatabase() {
    clientRepository.deleteAll();
    goalRepository.deleteAll();
    sdqRepository.deleteAll();
    gboRepository.deleteAll();
    reportingPeriodRepository.deleteAll();
  }
}
