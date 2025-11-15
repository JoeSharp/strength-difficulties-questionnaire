package uk.ratracejoe.sdq.model;

import java.time.Instant;
import java.util.UUID;

public record GboScore(
    UUID fileId,
    Assessor assessor,
    int periodIndex,
    Instant periodDate,
    Integer scoreIndex,
    Integer score) {}
