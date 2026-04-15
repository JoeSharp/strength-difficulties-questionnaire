package uk.ratracejoe.sdq.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.demographics.DemographicFilter;
import uk.ratracejoe.sdq.model.gbo.GboSubmission;
import uk.ratracejoe.sdq.model.gbo.Goal;
import uk.ratracejoe.sdq.model.gbo.GoalProgress;

public interface GoalService {

  Goal createGoal(Goal goal);

  void recordGoalScore(GboSubmission gbo);

  List<Goal> getGoalsForClient(UUID clientId);

  List<GoalProgress> getGoalsWithProgress(
      Assessor assessor,
      List<DemographicFilter> filters,
      int minProgress,
      LocalDate from,
      LocalDate to);

  Goal updateGoal(Goal goal);

  Goal getGoal(UUID goalId);
}
