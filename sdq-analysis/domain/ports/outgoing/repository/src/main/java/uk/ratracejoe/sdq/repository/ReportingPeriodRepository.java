package uk.ratracejoe.sdq.repository;

import java.util.List;
import java.util.UUID;
import uk.ratracejoe.sdq.model.ReportingPeriod;

public interface ReportingPeriodRepository {
  void save(ReportingPeriod period);

  List<ReportingPeriod> getForClient(UUID clientId);

  void deleteAll();
}
