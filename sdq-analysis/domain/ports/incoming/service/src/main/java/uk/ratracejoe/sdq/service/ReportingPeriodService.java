package uk.ratracejoe.sdq.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import uk.ratracejoe.sdq.model.ReportingPeriod;

public interface ReportingPeriodService {
  ReportingPeriod startPeriod(UUID clientId, Instant period);

  List<ReportingPeriod> getPeriodsForClient(UUID clientId);
}
