package uk.ratracejoe.sdq.model.sdq;

public enum Category {
  Emotional(Posture.Internalising),
  Peer(Posture.Internalising),
  Conduct(Posture.Externalising),
  HyperActivity(Posture.Externalising),
  ProSocial(Posture.ProSocial);

  final Posture posture;

  private Category(Posture posture) {
    this.posture = posture;
  }

  public Posture posture() {
    return posture;
  }
}
