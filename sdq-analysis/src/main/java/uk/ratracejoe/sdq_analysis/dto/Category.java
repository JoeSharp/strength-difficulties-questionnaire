package uk.ratracejoe.sdq_analysis.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Category {
    Emotional(Posture.Internalising),
    Peer(Posture.Internalising),
    Conduct(Posture.Externalising),
    HyperActivity(Posture.Externalising),
    ProSocial(Posture.ProSocial);

    final Posture posture;

    public Posture posture() {
        return posture;
    }
}
