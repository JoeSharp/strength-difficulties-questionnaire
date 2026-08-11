package uk.ratracejoe.sdq.model.demographics;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import uk.ratracejoe.sdq.model.EnumValue;

@RequiredArgsConstructor
public enum CareExperience {
  NO("No"),
  YES_ADOPTED("Yes - Adopted"),
  YES_CHILD_IN_CARE("Yes - Child in Care"),
  SGO("SGO"),
  KINSHIP("Kinship"),
  UNKNOWN("Unknown");
  final String display;

  public EnumValue enumValue() {
    return new EnumValue(name(), display);
  }

  public static CareExperience defaultValue() {
    return UNKNOWN;
  }

  public static CareExperience fromDisplay(String value) {
    for (CareExperience g : values()) {
      if (Objects.equals(g.display, value)) {
        return g;
      }
    }

    return defaultValue();
  }
}
