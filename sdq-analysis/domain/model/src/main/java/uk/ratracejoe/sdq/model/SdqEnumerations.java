package uk.ratracejoe.sdq.model;

import java.util.List;
import java.util.Map;

public record SdqEnumerations(Map<DemographicField, List<String>> demographics) {}
