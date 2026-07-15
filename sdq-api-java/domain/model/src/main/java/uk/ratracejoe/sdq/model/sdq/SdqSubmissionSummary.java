package uk.ratracejoe.sdq.model.sdq;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import uk.ratracejoe.sdq.model.Assessor;

@Builder
public record SdqSubmissionSummary(
    UUID clientId,
    Assessor assessor,
    LocalDate period,
    Map<String, Integer> categorySubTotals,
    Map<Posture, Integer> postureSubTotals,
    int totalDifficulties) {}
