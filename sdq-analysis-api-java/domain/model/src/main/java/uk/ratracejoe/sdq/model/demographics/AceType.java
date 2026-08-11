package uk.ratracejoe.sdq.model.demographics;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import uk.ratracejoe.sdq.model.EnumValue;

@RequiredArgsConstructor
public enum AceType {
  COMMUNITY("Community"),
  SOCIO_ECONOMIC("Socio-economic"),
  DISCRIMINATION("Discrimination & Social Exclusion"),
  HEALTH("Health"),
  EDUCATION("Education"),
  BEREAVEMENT("Bereavement & Loss"),
  DIGITAL_ONLINE("Digital/Online Adversities"),
  ENVIRONMENTAL("Environment Adversities"),
  CHILD_WELFARE("Child Welfare or Statutory Intervention Experiences"),
  GENERIC("Generic");
  final String display;

  public EnumValue enumValue() {
    return new EnumValue(name(), display);
  }

  public static AceType defaultValue() {
    return GENERIC;
  }

  public static AceType fromDisplay(String value) {
    for (AceType g : values()) {
      if (Objects.equals(g.display, value)) {
        return g;
      }
    }

    return defaultValue();
  }
}
