package uk.ratracejoe.sdq_analysis.dto;

import java.util.Map;
import java.util.UUID;

public record SdqScoresSummary(UUID uuid,
                               int period,
                               Assessor assessor,
                               Map<Category, Integer> categoryScores,
                               Map<Posture, Integer> postureScores,
                               int total) {
}
