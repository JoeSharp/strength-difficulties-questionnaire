package uk.ratracejoe.sdq.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.ReportingPeriod;
import uk.ratracejoe.sdq.model.demographics.DemographicFilter;
import uk.ratracejoe.sdq.model.sdq.SdqProgressSummary;
import uk.ratracejoe.sdq.model.sdq.SdqSubmission;
import uk.ratracejoe.sdq.model.sdq.SdqSubmissionSummary;

public interface SdqService {

  void recordResponse(SdqSubmission sdq) throws SdqException;

  SdqSubmission getSubmission(UUID periodId, Assessor assessor);

  SdqSubmissionSummary getSubmissionSummary(UUID periodId, Assessor assessor);

  List<ReportingPeriod> getReportingPeriods(UUID clientId);

  List<SdqProgressSummary> querySdqProgress(
      List<Assessor> assessors, List<DemographicFilter> filters, LocalDate from, LocalDate to);

  List<SdqSubmissionSummary> querySdqSummaries(
      List<Assessor> assessors, List<DemographicFilter> filters, LocalDate from, LocalDate to);

  SdqProgressSummary getSdqProgressForClient(UUID clientId, Assessor assessor);
}
