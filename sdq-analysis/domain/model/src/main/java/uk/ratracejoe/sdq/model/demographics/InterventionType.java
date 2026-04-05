package uk.ratracejoe.sdq.model.demographics;

import java.util.Objects;

public enum InterventionType {
  CCPT,
  CPRT,
  PTP,
  IA,
  UKKNOWN;

  public static InterventionType defaultValue() {
    return UKKNOWN;
  }

  public static InterventionType fromDisplay(String value) {
    for (InterventionType g : values()) {
      if (Objects.equals(g.name(), value)) {
        return g;
      }
    }

    return defaultValue();
  }
}
