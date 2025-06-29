package uk.ratracejoe.sdq_analysis.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DisabilityType {
    Physical("Physical Disability"),
    Sensory("Sensory Impairment (e.g. hearing or visual)"),
    Learning("Learning Disability"),
    Neurodiverse("Neurodivergence (e.g. ASD or ADHD)"),
    Mental("Mental Health Condition"),
    Chronic("Long Term or Chronic Illness"),
    Speech("Speech or Communication Difficulty"),
    Cognitive("Cognitive or Memory Impairment"),
    Other("Other"),
    UNKNOWN("UNKNOWN");

    final String display;

    String display() {
        return display;
    }
    public static DisabilityType fromDisplay(String input) {
        for (DisabilityType e : values()) {
            if (e.display.equals(input)) {
                return e;
            }
        }

        return UNKNOWN;
    }
}
