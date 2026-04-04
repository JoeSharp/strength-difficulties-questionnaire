package uk.ratracejoe.sdq.model;

import java.util.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EnglishAsAdditionalLanguage {
  YES("Yes"),
  NO("No"),
  PREFER_NOT_TO_SAY("Prefer Not To Say");

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
