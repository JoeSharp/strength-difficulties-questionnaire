package uk.ratracejoe.sdq_analysis.dto;

import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

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
    ACES("ACES"),
    FundingSource("Funding Source"),
    UNKNOWN("UNKNOWN");

    private final String heading;

    public static DemographicField fromHeading(String heading) {
        return Stream.of(values())
                .filter(v -> v.heading().equals(heading))
                .findFirst()
                .orElse(UNKNOWN);
    }

    public String heading() {
        return heading;
    }
}
