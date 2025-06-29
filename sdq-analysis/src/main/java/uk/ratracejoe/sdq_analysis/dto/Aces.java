package uk.ratracejoe.sdq_analysis.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Aces {
    VerbalAbuse("Verbal abuse"),
    PhysicalAbuse("Physical abuse"),
    SexualAbuse("Sexual abuse"),
    PhysicalNeglect("Physical neglect"),
    EmotionalNeglect("Emotional neglect"),
    ParentalSeparation("Parental separation"),
    HH_MentalIllness("Household mental illness"),
    HH_DomesticAbuse("Household domestic abuse"),
    HH_AlchoholAbuse("Household alcohol abuse"),
    HH_DrugAbuse("Household drug abuse"),
    Incareration("Incarceration of a household member"),
    UNKNOWN("UNKNOWN");

    final String display;

    public String display() {
        return display;
    }

    public static Aces fromDisplay(String input) {
        for (Aces e : values()) {
            if (e.display.equals(input)) {
                return e;
            }
        }

        return UNKNOWN;
    }
}
