package uk.ratracejoe.sdq.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.demographics.DemographicFilter;
import uk.ratracejoe.sdq.model.sdq.SdqProgress;
import uk.ratracejoe.sdq.model.sdq.SdqSubmission;

public interface SdqRepository {

  void save(SdqSubmission sdq) throws SdqException;

  SdqSubmission get(UUID periodId, Assessor assessor);

  int deleteAll();

  List<SdqProgress> getSdqProgress(Assessor assessor, LocalDate from, LocalDate to);

  List<SdqSubmission> getFilteredSdqs(
      Assessor assessor,
      String category,
      List<DemographicFilter> filters,
      LocalDate from,
      LocalDate to);
}
