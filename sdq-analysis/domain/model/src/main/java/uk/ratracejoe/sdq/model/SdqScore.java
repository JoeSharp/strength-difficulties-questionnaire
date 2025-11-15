package uk.ratracejoe.sdq.model;

import java.util.UUID;

public record SdqScore(
    UUID fileId, Integer period, Assessor assessor, Statement statement, Integer score) {}
