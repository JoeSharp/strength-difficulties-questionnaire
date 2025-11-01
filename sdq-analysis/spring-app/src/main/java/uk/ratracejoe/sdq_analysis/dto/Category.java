package uk.ratracejoe.sdq_analysis.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Category {
  Emotional(Posture.Internalising),
  Peer(Posture.Internalising),
  Conduct(Posture.Externalising),
  HyperActivity(Posture.Externalising),
  ProSocial(Posture.ProSocial);

  final Posture posture;

  @JsonValue
  public Posture posture() {
    return posture;
  }
}
