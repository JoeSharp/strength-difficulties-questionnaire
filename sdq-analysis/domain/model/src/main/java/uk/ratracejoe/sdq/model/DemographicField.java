package uk.ratracejoe.sdq.model;

import java.util.stream.Stream;

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

  private final String heading;

  private DemographicField(String heading) {
    this.heading = heading;
  }

  public static DemographicField fromHeading(String heading) {
    return Stream.of(values()).filter(v -> v.heading().equals(heading)).findFirst().orElse(UNKNOWN);
  }

  public String heading() {
    return heading;
  }
}
