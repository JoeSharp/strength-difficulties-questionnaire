package uk.ratracejoe.sdq.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import uk.ratracejoe.sdq.model.ReportingPeriod;

public interface ReportingPeriodService {
  ReportingPeriod startPeriod(UUID clientId, LocalDate period);

  List<ReportingPeriod> getPeriodsForClient(UUID clientId);
}
