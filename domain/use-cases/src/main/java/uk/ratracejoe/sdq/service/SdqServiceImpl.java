package uk.ratracejoe.sdq.service;

import java.time.LocalDate;
import java.util.*;
import lombok.RequiredArgsConstructor;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.ReportingPeriod;
import uk.ratracejoe.sdq.model.demographics.DemographicFilter;
import uk.ratracejoe.sdq.model.sdq.*;
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
    return forSubmission(getSubmission(periodId, assessor));
  }

  @Override
  public List<ReportingPeriod> getReportingPeriods(UUID clientId) {
    return reportingPeriodRepository.getForClient(clientId);
  }

  @Override
  public List<SdqSubmissionSummary> getSdqSummariesWithProgress(
      Assessor assessor,
      String category,
      List<DemographicFilter> filters,
      int minProgress,
      LocalDate from,
      LocalDate to) {
    return sdqRepository.getFilteredSdqs(assessor, category, filters, from, to).stream()
        .map(this::forSubmission)
        .toList();
  }

  public SdqSubmissionSummary forSubmission(SdqSubmission submission) {
    Map<String, Integer> byCategory = new HashMap<>();
    Map<Posture, Integer> byPosture = new EnumMap<>(Posture.class);

    for (SdqScore s : submission.scores()) {
      Category category = s.statement().category();
      Posture p = category.posture();
      int score = s.score();

      byCategory.merge(category.category(), score, Integer::sum);
      byPosture.merge(p, score, Integer::sum);
    }
    int totalDifficulties =
        submission.scores().stream()
            .filter(score -> !Posture.ProSocial.equals(score.statement().category().posture()))
            .mapToInt(SdqScore::score)
            .sum();
    return SdqSubmissionSummary.builder()
        .categorySubTotals(byCategory)
        .postureSubTotals(byPosture)
        .totalDifficulties(totalDifficulties)
        .build();
  }
}
