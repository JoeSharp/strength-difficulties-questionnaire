package uk.ratracejoe.sdq.model.gbo;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import uk.ratracejoe.sdq.model.EnumValue;

@RequiredArgsConstructor
public enum GoalType {
  EMOTIONAL("Emotional"),
  BEHAVIOURAL("Behavioural"),
  RELATIONAL("Relational"),
  REGULATORY_CAPACITY("Regulatory Capacity"),
  TRAUMA_RECOVERY("Trauma Recovery"),
  SELF_ESTEEM_CONFIDENCE("Self Esteem/Confidence"),
  UNKNOWN("Unknown");

  private final String display;

  public EnumValue enumValue() {
    return new EnumValue(name(), display);
  }

  public static GoalType defaultValue() {
    return UNKNOWN;
  }

  public static GoalType fromDisplay(String value) {
    for (GoalType g : values()) {
      if (Objects.equals(g.display, value)) {
        return g;
      }
    }

    return defaultValue();
  }
}
