package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.UUID;
import uk.ratracejoe.sdq.model.gbo.GboSubmission;
import uk.ratracejoe.sdq.model.gbo.Goal;

public interface GoalService {

  Goal createGoal(Goal goal);

  void recordGoalScore(GboSubmission gbo);

  List<Goal> getGoalsForClient(UUID clientId);

  Goal updateGoal(Goal goal);

  Goal getGoal(UUID goalId);
}
