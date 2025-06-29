package uk.ratracejoe.sdq_analysis.dto;

public enum InterventionType {
    CCPT,
    CPRT,
    PTP,
    IA,
    UNKNOWN;
    public static InterventionType fromDisplay(String input) {
        for (InterventionType e : values()) {
            if (e.name().equals(input)) {
                return e;
            }
        }

        return UNKNOWN;
    }
}
