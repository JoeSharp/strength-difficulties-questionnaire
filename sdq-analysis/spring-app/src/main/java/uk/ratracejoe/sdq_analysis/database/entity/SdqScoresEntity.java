package uk.ratracejoe.sdq_analysis.database.entity;

import java.util.UUID;
import uk.ratracejoe.sdq_analysis.dto.Assessor;
import uk.ratracejoe.sdq_analysis.dto.Statement;

public record SdqScoresEntity(
    UUID fileUUID, Integer period, Assessor assessor, Statement statement, Integer score) {}
