package uk.ratracejoe.sdq.repository;

import java.util.List;
import java.util.UUID;
import uk.ratracejoe.sdq.model.gbo.Goal;

public interface GoalRepository {
  void save(Goal goal);

  List<Goal> getForClient(UUID clientId);

  int deleteAll();

  int update(Goal goal);

  Goal get(UUID goalId);
}
