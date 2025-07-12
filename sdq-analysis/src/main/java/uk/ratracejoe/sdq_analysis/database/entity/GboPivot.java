package uk.ratracejoe.sdq_analysis.database.entity;

import uk.ratracejoe.sdq_analysis.dto.Assessor;
import uk.ratracejoe.sdq_analysis.dto.Category;
import uk.ratracejoe.sdq_analysis.dto.Posture;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record GboPivot(UUID uuid,
                       Assessor assessor,
                       int periodIndex,
                       Instant periodDate,
                       Map<Integer, Integer> scores) {
}
