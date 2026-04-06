package uk.ratracejoe.sdq.model;

import lombok.Builder;

@Builder
public record GboParsedScore(Integer index, Integer score) {}
