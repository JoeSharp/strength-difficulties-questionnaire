package uk.ratracejoe.sdq_analysis.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record GboPeriod(Integer periodIndex, Instant periodDate, Map<Integer, Integer> scores) {
}
