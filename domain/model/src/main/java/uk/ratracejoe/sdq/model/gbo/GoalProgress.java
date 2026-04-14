package uk.ratracejoe.sdq.model.gbo;

import lombok.Builder;

@Builder
public record GoalProgress(Goal goal, Integer firstScore, Integer lastScore) {}
