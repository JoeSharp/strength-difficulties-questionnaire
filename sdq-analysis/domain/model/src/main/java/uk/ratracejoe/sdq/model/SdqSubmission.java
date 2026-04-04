package uk.ratracejoe.sdq.model;

import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record SdqSubmission(
    UUID clientId, Integer period, Assessor assessor, List<SdqScore> scores) {}
