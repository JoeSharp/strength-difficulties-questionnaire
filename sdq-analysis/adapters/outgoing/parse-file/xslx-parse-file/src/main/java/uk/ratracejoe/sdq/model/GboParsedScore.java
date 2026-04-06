package uk.ratracejoe.sdq.model;

import java.time.Instant;
import lombok.Builder;

@Builder
public record GboParsedScore(Instant date, int index, int score) {}
