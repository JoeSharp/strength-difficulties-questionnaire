package uk.ratracejoe.sdq_analysis.database.entity;

import java.time.Instant;
import java.util.UUID;
import uk.ratracejoe.sdq_analysis.dto.Assessor;

public record GboEntity(
    UUID fileUuid,
    Assessor assessor,
    Integer periodIndex,
    Instant periodDate,
    Integer scoreIndex,
    Integer score) {}
