package uk.ratracejoe.sdq.model;

import java.time.Instant;
import java.util.List;
import lombok.Builder;

@Builder
public record GboParsedPeriod(Assessor assessor, Instant period, List<GboParsedScore> scores) {}
