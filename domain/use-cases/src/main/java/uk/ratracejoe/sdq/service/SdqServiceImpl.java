package uk.ratracejoe.sdq.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.ReportingPeriod;
import uk.ratracejoe.sdq.model.demographics.DemographicFilter;
import uk.ratracejoe.sdq.model.sdq.SdqProgressSummary;
import uk.ratracejoe.sdq.model.sdq.SdqSubmission;
import uk.ratracejoe.sdq.model.sdq.SdqSubmissionSummary;
import uk.ratracejoe.sdq.repository.ReportingPeriodRepository;
import uk.ratracejoe.sdq.repository.SdqRepository;

@RequiredArgsConstructor
public class SdqServiceImpl implements SdqService {
  private final SdqRepository sdqRepository;
  private final ReportingPeriodRepository reportingPeriodRepository;

  public void recordResponse(SdqSubmission sdq) throws SdqException {
    sdqRepository.save(sdq);
  }

  @Override
  public SdqSubmission getSubmission(UUID periodId, Assessor assessor) {
    return sdqRepository.get(periodId, assessor);
  }

  @Override
  public SdqSubmissionSummary getSubmissionSummary(UUID periodId, Assessor assessor) {
    return sdqRepository.getSummary(periodId, assessor);
  }

  @Override
  public List<ReportingPeriod> getReportingPeriods(UUID clientId) {
    return reportingPeriodRepository.getForClient(clientId);
  }

  @Override
  public List<SdqProgressSummary> querySdqProgress(
      List<Assessor> assessors, List<DemographicFilter> filters, LocalDate from, LocalDate to) {
    return sdqRepository.getSdqProgress(assessors, filters, from, to);
  }

  @Override
  public List<SdqSubmissionSummary> querySdqSummaries(
      List<Assessor> assessors, List<DemographicFilter> filters, LocalDate from, LocalDate to) {
    return sdqRepository.getFiltered(assessors, filters, from, to);
  }

  @Override
  public SdqProgressSummary getSdqProgressForClient(UUID clientId, Assessor assessor) {
    return sdqRepository.getProgressForClient(clientId, assessor);
  }
}
