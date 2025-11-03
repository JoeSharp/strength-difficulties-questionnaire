package uk.ratracejoe.sdq.entity;

import java.util.UUID;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.Statement;

public record SdqScoresEntity(
    UUID fileUUID, Integer period, Assessor assessor, Statement statement, Integer score) {}
