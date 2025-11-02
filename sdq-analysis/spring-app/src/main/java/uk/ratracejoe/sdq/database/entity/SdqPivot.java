package uk.ratracejoe.sdq.database.entity;

import java.util.Map;
import java.util.UUID;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.Category;
import uk.ratracejoe.sdq.model.Posture;

public record SdqPivot(
    UUID uuid,
    int period,
    Assessor assessor,
    Map<Category, Integer> categoryScores,
    Map<Posture, Integer> postureScores,
    int total) {}
