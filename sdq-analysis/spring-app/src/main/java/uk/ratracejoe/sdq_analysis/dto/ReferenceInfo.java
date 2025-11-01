package uk.ratracejoe.sdq_analysis.dto;

import java.util.List;
import java.util.Map;

public record ReferenceInfo(
    List<Map<String, Object>> categories,
    List<Map<String, Object>> statements,
    List<Map<String, Object>> postures,
    Map<DemographicField, List<String>> demographicFields) {}
