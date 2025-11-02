package uk.ratracejoe.sdq.dto;

import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public enum DemographicField {
  Gender("Gender/Sex"),
  Council("Council"),
  Ethnicity("Ethnicity"),
  EAL("English as an Additional Language"),
  DisabilityStatus("Disability Status"),
  DisabilityType("Disability Type"),
  CareExperience("Care Experience"),
  InterventionType("Intervention Type"),
  AdditionalInterventionType("Additional Intervention Type"),
  ACES("ACES"),
  FundingSource("Funding source"),
  UNKNOWN("UNKNOWN");

  private static final Logger LOGGER = LoggerFactory.getLogger(DemographicField.class);
  private final String heading;

  public static DemographicField fromHeading(String heading) {
    return Stream.of(values())
        .filter(v -> v.heading().equals(heading))
        .findFirst()
        .orElseGet(
            () -> {
              LOGGER.warn("Unknown demographic field heading {}", heading);
              return UNKNOWN;
            });
  }

  public String heading() {
    return heading;
  }
}
