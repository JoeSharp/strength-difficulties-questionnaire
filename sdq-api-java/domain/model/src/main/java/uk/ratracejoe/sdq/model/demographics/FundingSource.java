package uk.ratracejoe.sdq.model.demographics;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import uk.ratracejoe.sdq.model.EnumValue;

@RequiredArgsConstructor
public enum FundingSource {
  EHCP("EHCP"),
  PEP("PEP"),
  ASGSF("ASGSF"),
  PRIVATE("Private"),
  OTHER_CHARITABLE("Other Charitable"),
  SUBSIDISED_SESSION_FUND("Subsidised Session Fund"),

  PROJECT("Project"),
  UNKNOWN("Unknown");

  final String display;

  public EnumValue enumValue() {
    return new EnumValue(name(), display);
  }

  public static FundingSource defaultValue() {
    return UNKNOWN;
  }

  public static FundingSource fromDisplay(String value) {
    for (FundingSource g : values()) {
      if (Objects.equals(g.display, value)) {
        return g;
      }
    }

    return defaultValue();
  }
}
