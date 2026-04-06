package uk.ratracejoe.sdq.model.gbo;

import java.util.UUID;
import lombok.Builder;

@Builder
public record GboScore(UUID goalId, Integer score) {}
