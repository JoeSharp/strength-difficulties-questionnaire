package uk.ratracejoe.sdq_analysis.database.entity;

import uk.ratracejoe.sdq_analysis.dto.Assessor;
import uk.ratracejoe.sdq_analysis.dto.Statement;

import java.util.UUID;

public record SdqScoresEntity(UUID fileUUID,
                              int period,
                              Assessor assessor,
                              Statement statement,
                              Integer score) {
}
