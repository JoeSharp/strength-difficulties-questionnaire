package uk.ratracejoe.sdq.database.entity;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import uk.ratracejoe.sdq.dto.Assessor;

public record GboPivot(
    UUID uuid,
    Assessor assessor,
    int periodIndex,
    Instant periodDate,
    Map<Integer, Integer> scores) {}
