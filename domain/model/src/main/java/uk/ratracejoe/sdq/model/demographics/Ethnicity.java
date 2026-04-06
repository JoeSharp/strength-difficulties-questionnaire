package uk.ratracejoe.sdq.model.demographics;

import java.util.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Ethnicity {
  WHITE_BRITISH("White British"),
  WHITE_EUROPEAN("White European"),
  MIXED("Mixed"),
  ASIAN("Asian/Asian British"),
  BLACK("Black/African/Caribbean/Black British"),
  TRAVELLER("Traveller"),
  OTHER("Other");

  final String display;

  public static Ethnicity defaultValue() {
    return OTHER;
  }

  public static Ethnicity fromDisplay(String value) {
    for (Ethnicity g : values()) {
      if (Objects.equals(g.display, value)) {
        return g;
      }
    }

    return defaultValue();
  }
}
