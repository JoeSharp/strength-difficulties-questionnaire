package uk.ratracejoe.sdq.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import uk.ratracejoe.sdq.model.ReportingPeriod;
import uk.ratracejoe.sdq.repository.ReportingPeriodRepository;

@RequiredArgsConstructor
public class ReportingPeriodServiceImpl implements ReportingPeriodService {
  private final ReportingPeriodRepository repository;

  @Override
  public ReportingPeriod startPeriod(UUID clientId, LocalDate period) {
    ReportingPeriod created =
        ReportingPeriod.builder()
            .periodId(UUID.randomUUID())
            .clientId(clientId)
            .period(period)
            .build();
    repository.save(created);
    return created;
  }

  @Override
  public List<ReportingPeriod> getPeriodsForClient(UUID clientId) {
    return repository.getForClient(clientId);
  }
}
