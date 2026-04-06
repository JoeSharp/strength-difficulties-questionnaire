package uk.ratracejoe.sdq.model.demographics;

import java.util.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Gender {
  MALE("Male"),
  FEMALE("Female"),
  NON_BINARY("Non-binary"),
  OTHER("Other"),
  PREFER_NOT_TO_SAY("Prefer Not To Say");

  final String display;

  public static Gender defaultValue() {
    return PREFER_NOT_TO_SAY;
  }

  public static Gender fromDisplay(String value) {
    for (Gender g : values()) {
      if (Objects.equals(g.display, value)) {
        return g;
      }
    }

    return defaultValue();
  }
}
