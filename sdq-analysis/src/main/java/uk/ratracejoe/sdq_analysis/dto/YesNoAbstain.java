package uk.ratracejoe.sdq_analysis.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum YesNoAbstain {
    Yes("Yes"),
    No("No"),
    PreferNotToSay("Prefer Not to Say"),
    UNKNOWN("UNKNOWN");

    final String display;

    public String display() {
        return display;
    }
    public static YesNoAbstain fromDisplay(String input) {
        for (YesNoAbstain e : values()) {
            if (e.display.equals(input)) {
                return e;
            }
        }

        return UNKNOWN;
    }
}
