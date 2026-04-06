package uk.ratracejoe.sdq.model.demographics;

import java.util.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Council {
  CHELTENHAM("Cheltenham"),
  GLOUCESTER_CITY("Gloucester City"),
  STROUD("Stroud"),
  TEWKESBURY("Tewksbury"),
  FOREST_OF_DEAN("Forest of Dean"),
  NORTH_COTSWOLDS("North Cotswolds"),
  OUT_OF_COUNTY("Out of County"),
  UNKNOWN("Unknown");

  final String display;

  public static Council defaultValue() {

    return UNKNOWN;
  }

  public static Council fromDisplay(String value) {
    for (Council g : values()) {
      if (Objects.equals(g.display, value)) {
        return g;
      }
    }

    return defaultValue();
  }
}
