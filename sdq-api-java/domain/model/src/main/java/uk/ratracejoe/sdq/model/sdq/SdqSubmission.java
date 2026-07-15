package uk.ratracejoe.sdq.model.sdq;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import uk.ratracejoe.sdq.model.Assessor;

@Builder
public record SdqSubmission(UUID periodId, Assessor assessor, List<SdqScore> scores) {}
