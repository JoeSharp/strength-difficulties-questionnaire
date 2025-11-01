package uk.ratracejoe.sdq_analysis.dto;

import java.util.Map;
import java.util.UUID;

public record SdqSummary(
    UUID uuid,
    int period,
    Map<Category, Integer> categoryScores,
    Map<Posture, Integer> postureScores,
    int total) {}
