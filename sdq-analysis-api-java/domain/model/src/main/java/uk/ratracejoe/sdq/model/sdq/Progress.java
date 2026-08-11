package uk.ratracejoe.sdq.model.sdq;

import lombok.Builder;

@Builder
public record Progress(int last, int delta, int first) {}
