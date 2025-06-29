package uk.ratracejoe.sdq_analysis.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Gender {
    Male("Male"),
    Female("Female"),
    NonBinary("Non-binary"),
    Other("Other"),
    PeferNotToSay("Prefer not to say"),
    UNKNOWN("UNKNOWN");

    final String display;

    String display() {
        return display;
    }
    public static Gender fromDisplay(String input) {
        for (Gender e : values()) {
            if (e.display.equals(input)) {
                return e;
            }
        }

        return UNKNOWN;
    }
}
