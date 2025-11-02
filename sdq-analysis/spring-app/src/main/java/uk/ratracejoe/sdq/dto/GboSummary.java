package uk.ratracejoe.sdq.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record GboSummary(
    UUID uuid, int periodIndex, Instant periodDate, Map<Integer, Integer> scores) {}
