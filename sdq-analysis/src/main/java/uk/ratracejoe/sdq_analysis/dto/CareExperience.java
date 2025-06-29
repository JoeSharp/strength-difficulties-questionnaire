package uk.ratracejoe.sdq_analysis.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CareExperience {
    No("No"),
    YesAdopted("Yes - Adopted"),
    YesInCare("Yes - Child in Care"),
    SGO("SGO"),
    Kinship("Kinship"),
    UNKNOWN("UNKNOWN");

    final String display;

    String display() {
        return display;
    }
    public static CareExperience fromDisplay(String input) {
        for (CareExperience e : values()) {
            if (e.display.equals(input)) {
                return e;
            }
        }

        return UNKNOWN;
    }
}
