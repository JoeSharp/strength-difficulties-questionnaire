package uk.ratracejoe.sdq.service;

import uk.ratracejoe.sdq.model.gbo.GboSubmission;
import uk.ratracejoe.sdq.model.gbo.Goal;

public interface GoalService {

  Goal createGoal(Goal goal);

  void recordGoalScore(GboSubmission gbo);
}
