package uk.ratracejoe.sdq.model.sdq;

import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import uk.ratracejoe.sdq.model.Assessor;

@Builder
public record SdqProgressSummary(
    UUID clientId,
    Assessor assessor,
    Map<String, Progress> categoryProgress,
    Map<Posture, Progress> postureProgress,
    Progress totalDifficulties) {}
