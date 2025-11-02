package uk.ratracejoe.sdq.dto;

import java.util.List;
import java.util.Map;

public record SdqPeriod(Integer periodIndex, Map<Assessor, List<StatementResponse>> responses) {}
