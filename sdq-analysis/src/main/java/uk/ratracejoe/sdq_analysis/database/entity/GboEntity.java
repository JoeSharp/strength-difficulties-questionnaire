package uk.ratracejoe.sdq_analysis.database.entity;

import uk.ratracejoe.sdq_analysis.dto.Assessor;

import java.time.Instant;
import java.util.UUID;

public record GboEntity(UUID fileUuid,
                        Assessor assessor,
                        Integer periodIndex,
                        Instant periodDate,
                        Integer scoreIndex,
                        Integer score) {
}
