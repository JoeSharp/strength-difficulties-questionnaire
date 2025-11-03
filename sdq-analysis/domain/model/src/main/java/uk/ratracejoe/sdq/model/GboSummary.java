package uk.ratracejoe.sdq.model;

import java.time.Instant;
import java.util.UUID;

public record GboSummary(
    UUID uuid, int periodIndex, Instant periodDate, Integer scoreIndex, Integer score) {}
