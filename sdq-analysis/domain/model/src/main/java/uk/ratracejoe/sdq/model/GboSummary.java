package uk.ratracejoe.sdq.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record GboSummary(
    UUID uuid, int periodIndex, Instant periodDate, Map<Integer, Integer> scores) {}
