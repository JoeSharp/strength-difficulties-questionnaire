package uk.ratracejoe.sdq.database.entity;

import java.time.Instant;
import java.util.UUID;
import uk.ratracejoe.sdq.dto.Assessor;

public record GboEntity(
    UUID fileUuid,
    Assessor assessor,
    Integer periodIndex,
    Instant periodDate,
    Integer scoreIndex,
    Integer score) {}
