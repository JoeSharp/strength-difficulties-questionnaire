package uk.ratracejoe.sdq.model.demographics;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import uk.ratracejoe.sdq.model.EnumValue;

@RequiredArgsConstructor
public enum EnglishAsAdditionalLanguage {
  YES("Yes"),
  NO("No"),
  PREFER_NOT_TO_SAY("Prefer Not To Say");

  public EnumValue enumValue() {
    return new EnumValue(name(), display);
  }

  final String display;

  public static EnglishAsAdditionalLanguage defaultValue() {
    return PREFER_NOT_TO_SAY;
  }

  public static EnglishAsAdditionalLanguage fromDisplay(String value) {
    for (EnglishAsAdditionalLanguage g : values()) {
      if (Objects.equals(g.display, value)) {
        return g;
      }
    }

    return defaultValue();
  }
}
