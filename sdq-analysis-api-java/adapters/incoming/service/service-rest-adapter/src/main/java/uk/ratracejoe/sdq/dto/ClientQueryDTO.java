package uk.ratracejoe.sdq.dto;

import java.util.List;
import lombok.Builder;
import uk.ratracejoe.sdq.model.demographics.DemographicFilter;

@Builder
public record ClientQueryDTO(String partialName, List<DemographicFilter> filters) {}
