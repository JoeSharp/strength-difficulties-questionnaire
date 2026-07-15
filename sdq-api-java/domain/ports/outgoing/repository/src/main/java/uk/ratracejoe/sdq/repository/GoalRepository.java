package uk.ratracejoe.sdq.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.demographics.DemographicFilter;
import uk.ratracejoe.sdq.model.gbo.Goal;
import uk.ratracejoe.sdq.model.gbo.GoalProgress;
import uk.ratracejoe.sdq.model.gbo.GoalType;

public interface GoalRepository {
  void save(Goal goal);

  List<Goal> getForClient(UUID clientId);

  int deleteAll();

  List<GoalProgress> getGoalsWithProgress(
      List<Assessor> assessors,
      List<DemographicFilter> filters,
      int minProgress,
      List<GoalType> goalTypes,
      LocalDate from,
      LocalDate to);

  int update(Goal goal);

  Goal get(UUID goalId);

  GoalProgress getGoalProgress(UUID goalId, Assessor assessor);

  List<GoalProgress> getGoalsProgressForClient(UUID clientId, Assessor assessor);
}
