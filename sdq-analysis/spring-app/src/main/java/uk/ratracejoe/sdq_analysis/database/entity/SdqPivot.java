package uk.ratracejoe.sdq_analysis.database.entity;

import java.util.Map;
import java.util.UUID;
import uk.ratracejoe.sdq_analysis.dto.Assessor;
import uk.ratracejoe.sdq_analysis.dto.Category;
import uk.ratracejoe.sdq_analysis.dto.Posture;

public record SdqPivot(
    UUID uuid,
    int period,
    Assessor assessor,
    Map<Category, Integer> categoryScores,
    Map<Posture, Integer> postureScores,
    int total) {}
