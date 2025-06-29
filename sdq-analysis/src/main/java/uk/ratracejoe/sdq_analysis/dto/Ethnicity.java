package uk.ratracejoe.sdq_analysis.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Ethnicity {
    White("White"),
    Mixed("Mixed"),
    Asian("Asian/Asian British"),
    Black("Black/African/Caribbean/Black British"),
    Other("Other"),
    UNKNOWN("UNKNOWN");

    final String display;

    String display() {
        return display;
    }

    public static Ethnicity fromDisplay(String input) {
        for (Ethnicity e : values()) {
            if (e.display.equals(input)) {
                return e;
            }
        }

        return UNKNOWN;
    }
}
