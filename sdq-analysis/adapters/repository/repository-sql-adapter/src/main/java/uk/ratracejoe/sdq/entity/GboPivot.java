package uk.ratracejoe.sdq.entity;

import java.time.Instant;
import java.util.UUID;
import uk.ratracejoe.sdq.model.Assessor;

public record GboPivot(
    UUID uuid,
    Assessor assessor,
    int periodIndex,
    Instant periodDate,
    Integer scoreIndex,
    Integer score) {}
