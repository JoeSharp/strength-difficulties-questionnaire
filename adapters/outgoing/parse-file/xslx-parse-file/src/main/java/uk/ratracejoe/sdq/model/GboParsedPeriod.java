package uk.ratracejoe.sdq.model;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record GboParsedPeriod(Assessor assessor, LocalDate period, List<GboParsedScore> scores) {}
