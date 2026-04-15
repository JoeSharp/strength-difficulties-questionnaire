package uk.ratracejoe.sdq.model.demographics;

import java.util.List;

public record DemographicFilter(DemographicField field, List<String> values) {}
