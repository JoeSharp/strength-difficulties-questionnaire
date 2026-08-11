package uk.ratracejoe.sdq.model.sdq;

import lombok.Builder;

@Builder
public record SdqScore(Statement statement, Integer score) {}
