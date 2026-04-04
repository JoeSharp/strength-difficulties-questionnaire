package uk.ratracejoe.sdq.model;

import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record SdqSubmission(UUID periodId, Assessor assessor, List<SdqScore> scores) {}
