package uk.ratracejoe.sdq.model;

import java.util.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DisabilityType {
  PHYSICAL("Physical"),
  SENSORY("Sensory Impairment (e.g. hearing or visual)"),
  LEARNING("Learning"),
  NEURODIVERSE("Neurodivergence (e.g. ASD or ADHD)"),
  MENTAL_HEALTH_CONDITION("Mental Health Condition"),
  CHRONIC("Long Term or Chronic Illness"),
  SPEECH_OR_COMMUNICATION("Speech or Communication"),
  COGNITIVE_OR_MEMORY("Cognitive or Memory Impairment"),
  OTHER("Other"),
  NOT_APPLICABLE("N/A");

  final String display;

  public static DisabilityType defaultValue() {
    return NOT_APPLICABLE;
  }

  public static DisabilityType fromDisplay(String value) {
    for (DisabilityType g : values()) {
      if (Objects.equals(g.display, value)) {
        return g;
      }
    }

    return defaultValue();
  }
}
