package uk.ratracejoe.sdq.model.demographics;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import uk.ratracejoe.sdq.model.EnumValue;

@RequiredArgsConstructor
public enum DisabilityStatus {
  DISABILITY("Disability"),
  NO_DISABILITY("No Disability"),
  PREFER_NOT_TO_SAY("Prefer Not To Say");

  final String display;

  public EnumValue enumValue() {
    return new EnumValue(name(), display);
  }

  public static DisabilityStatus defaultValue() {
    return PREFER_NOT_TO_SAY;
  }

  public static DisabilityStatus fromDisplay(String value) {
    for (DisabilityStatus g : values()) {
      if (Objects.equals(g.display, value)) {
        return g;
      }
    }

    return defaultValue();
  }
}
