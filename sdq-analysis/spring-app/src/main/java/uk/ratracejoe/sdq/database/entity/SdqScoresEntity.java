package uk.ratracejoe.sdq.database.entity;

import java.util.UUID;
import uk.ratracejoe.sdq.dto.Assessor;
import uk.ratracejoe.sdq.dto.Statement;

public record SdqScoresEntity(
    UUID fileUUID, Integer period, Assessor assessor, Statement statement, Integer score) {}
