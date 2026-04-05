package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.ReportingPeriod;
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
    SdqSubmission submission = getSubmission(periodId, assessor);
    Map<Category, Integer> scoresByCategory =
        submission.scores().stream()
            .collect(
                Collectors.groupingBy(
                    s -> s.statement().category(), Collectors.summingInt(SdqScore::score)));
    Map<Posture, Integer> scoresByPosture =
        submission.scores().stream()
            .collect(
                Collectors.groupingBy(
                    s -> s.statement().category().posture(),
                    Collectors.summingInt(SdqScore::score)));
    int totalDifficulties =
        submission.scores().stream()
            .filter(score -> !Category.ProSocial.equals(score.statement().category()))
            .mapToInt(SdqScore::score)
            .sum();
    return SdqSubmissionSummary.builder()
        .categorySubTotals(scoresByCategory)
        .postureSubTotals(scoresByPosture)
        .totalDifficulties(totalDifficulties)
        .build();
  }

  @Override
  public List<ReportingPeriod> getReportingPeriods(UUID clientId) {
    return reportingPeriodRepository.getForClient(clientId);
  }
}
