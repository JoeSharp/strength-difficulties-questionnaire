package uk.ratracejoe.sdq.model.gbo;

import lombok.Builder;
import uk.ratracejoe.sdq.model.Assessor;

@Builder
public record GoalProgress(Goal goal, Assessor assessor, Integer firstScore, Integer lastScore) {}
