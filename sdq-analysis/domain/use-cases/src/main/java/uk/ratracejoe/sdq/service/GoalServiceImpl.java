package uk.ratracejoe.sdq.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.gbo.GboSubmission;
import uk.ratracejoe.sdq.model.gbo.Goal;
import uk.ratracejoe.sdq.repository.GboRepository;
import uk.ratracejoe.sdq.repository.GoalRepository;

@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
  private final GboRepository gboRepository;
  private final GoalRepository goalRepository;

  @Override
  public Goal createGoal(Goal goal) {
    Goal toCreate =
        Goal.builder()
            .goalId(UUID.randomUUID())
            .clientId(goal.clientId())
            .description(goal.description())
            .build();
    goalRepository.save(toCreate);
    return toCreate;
  }

  @Override
  public void recordGoalScore(GboSubmission gbo) throws SdqException {
    gboRepository.save(gbo);
  }
}
