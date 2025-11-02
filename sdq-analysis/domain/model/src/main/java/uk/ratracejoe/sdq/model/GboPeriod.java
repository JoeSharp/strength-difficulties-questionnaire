package uk.ratracejoe.sdq.model;

import java.time.Instant;
import java.util.Map;

public record GboPeriod(Integer periodIndex, Instant periodDate, Map<Integer, Integer> scores) {}
