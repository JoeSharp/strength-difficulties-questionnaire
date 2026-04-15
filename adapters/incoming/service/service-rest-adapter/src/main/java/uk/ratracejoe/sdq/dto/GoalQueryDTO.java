package uk.ratracejoe.sdq.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.demographics.DemographicFilter;

@Builder
public record GoalQueryDTO(
    Assessor assessor,
    List<DemographicFilter> filters,
    int minProgress,
    LocalDate from,
    LocalDate to) {}
