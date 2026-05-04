package uk.ratracejoe.sdq.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.demographics.DemographicFilter;

@Builder
public record SdqFilterDTO(
    Assessor assessor, List<DemographicFilter> filters, LocalDate from, LocalDate to) {}
