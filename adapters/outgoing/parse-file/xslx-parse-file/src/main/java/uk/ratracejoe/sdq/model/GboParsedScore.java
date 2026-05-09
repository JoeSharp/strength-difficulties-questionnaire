package uk.ratracejoe.sdq.model;

import lombok.Builder;
import uk.ratracejoe.sdq.model.gbo.GoalType;

@Builder
public record GboParsedScore(GoalType goalType, Integer index, Integer score) {}
