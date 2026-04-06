package uk.ratracejoe.sdq.dto;

import java.util.List;
import java.util.Map;
import uk.ratracejoe.sdq.model.demographics.DemographicField;

public record ReferenceInfoDTO(
    List<Map<String, Object>> categories,
    List<Map<String, Object>> statements,
    List<Map<String, Object>> postures,
    Map<DemographicField, List<String>> demographicFields) {}
