package uk.ratracejoe.sdq.dto;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.demographics.DemographicFilter;
import uk.ratracejoe.sdq.model.gbo.GoalType;

@Builder
public record GoalQueryDTO(
    List<Assessor> assessors,
    List<DemographicFilter> filters,
    int minProgress,
    List<GoalType> goalTypes,
    LocalDate from,
    LocalDate to) {
  public List<Assessor> assessors() {
    return Optional.ofNullable(assessors).orElseGet(Collections::emptyList);
  }

  public List<DemographicFilter> filters() {
    return Optional.ofNullable(filters).orElseGet(Collections::emptyList);
  }

  public List<GoalType> goalTypes() {
    return Optional.ofNullable(goalTypes).orElseGet(Collections::emptyList);
  }
}
