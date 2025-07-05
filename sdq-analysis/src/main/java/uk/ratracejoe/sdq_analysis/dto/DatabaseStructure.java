package uk.ratracejoe.sdq_analysis.dto;

import java.util.List;
import java.util.Map;

public record DatabaseStructure(Map<DemographicField, List<String>> demographics) {
}
