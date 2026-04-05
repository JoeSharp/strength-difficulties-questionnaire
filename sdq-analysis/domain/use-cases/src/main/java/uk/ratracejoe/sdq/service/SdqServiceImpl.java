package uk.ratracejoe.sdq.service;

import java.util.*;
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
    Map<Category, Integer> byCategory = new EnumMap<>(Category.class);
    Map<Posture, Integer> byPosture = new EnumMap<>(Posture.class);

    for (SdqScore s : submission.scores()) {
      Category c = s.statement().category();
      Posture p = c.posture();
      int score = s.score();

      byCategory.merge(c, score, Integer::sum);
      byPosture.merge(p, score, Integer::sum);
    }
    int totalDifficulties =
        submission.scores().stream()
            .filter(score -> !Category.ProSocial.equals(score.statement().category()))
            .mapToInt(SdqScore::score)
            .sum();
    return SdqSubmissionSummary.builder()
        .categorySubTotals(byCategory)
        .postureSubTotals(byPosture)
        .totalDifficulties(totalDifficulties)
        .build();
  }

  @Override
  public List<ReportingPeriod> getReportingPeriods(UUID clientId) {
    return reportingPeriodRepository.getForClient(clientId);
  }
}
