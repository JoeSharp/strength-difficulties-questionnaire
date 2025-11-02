package uk.ratracejoe.sdq.dto;

import java.time.Instant;
import java.util.Map;

public record GboPeriod(Integer periodIndex, Instant periodDate, Map<Integer, Integer> scores) {}
