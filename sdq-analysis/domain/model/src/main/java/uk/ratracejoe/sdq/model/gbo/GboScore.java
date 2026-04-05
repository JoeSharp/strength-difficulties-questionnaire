package uk.ratracejoe.sdq.model.gbo;

import lombok.Builder;

@Builder
public record GboScore(Integer scoreIndex, Integer score) {}
