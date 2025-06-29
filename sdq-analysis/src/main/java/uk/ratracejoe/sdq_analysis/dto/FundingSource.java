package uk.ratracejoe.sdq_analysis.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FundingSource {
    EHCP("EHCP"),
    PEP("PEP"),
    A_SG("A&SG"),
    Private("Private"),
    OtherCharitable("Other Charitable"),
    SubsidisedSessionFund("Subsidised Session Fund"),
    Project("Project"),
    UNKNOWN("UNKNOWN");

    final String display;

    public String display() {
        return display;
    }
    public static FundingSource fromDisplay(String input) {
        for (FundingSource e : values()) {
            if (e.display.equals(input)) {
                return e;
            }
        }

        return UNKNOWN;
    }
}
