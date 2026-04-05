package uk.ratracejoe.sdq.model.gbo;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import uk.ratracejoe.sdq.model.Assessor;

@Builder
public record GboSubmission(
    UUID clientId, Instant period, Assessor assessor, List<GboScore> scores) {}
