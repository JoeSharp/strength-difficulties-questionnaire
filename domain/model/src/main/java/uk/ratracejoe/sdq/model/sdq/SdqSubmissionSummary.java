package uk.ratracejoe.sdq.model.sdq;

import java.util.Map;
import java.util.UUID;
import lombok.Builder;

@Builder
public record SdqSubmissionSummary(
    UUID clientId,
    UUID periodid,
    Map<String, Integer> categorySubTotals,
    Map<Posture, Integer> postureSubTotals,
    int totalDifficulties) {}
