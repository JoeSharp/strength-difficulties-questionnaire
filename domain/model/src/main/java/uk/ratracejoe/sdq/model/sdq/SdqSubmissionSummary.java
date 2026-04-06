package uk.ratracejoe.sdq.model.sdq;

import java.util.Map;
import lombok.Builder;

@Builder
public record SdqSubmissionSummary(
    Map<Category, Integer> categorySubTotals,
    Map<Posture, Integer> postureSubTotals,
    int totalDifficulties) {}
