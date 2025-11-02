package uk.ratracejoe.sdq.dto;

import java.util.List;
import java.util.Map;

public record SdqEnumerations(Map<DemographicField, List<String>> demographics) {}
