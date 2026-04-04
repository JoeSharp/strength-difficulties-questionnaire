package uk.ratracejoe.sdq.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record GboSubmission(
    UUID clientId, Instant period, Assessor assessor, List<GboScore> scores) {}
