package uk.ratracejoe.sdq.rest;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.ratracejoe.sdq.model.gbo.GboSubmission;
import uk.ratracejoe.sdq.model.gbo.Goal;
import uk.ratracejoe.sdq.service.GoalService;

@RestController
@RequestMapping("/api/goal")
@RequiredArgsConstructor
public class GoalController {
  private final GoalService goalService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Goal createGoal(@RequestBody Goal goal) {
    return goalService.createGoal(goal);
  }

  @GetMapping("/forClient/{clientId}")
  public List<Goal> getGoalsForClient(@PathVariable UUID clientId) {
    return goalService.getGoalsForClient(clientId);
  }

  @GetMapping("/{goalId}")
  public Goal getGoal(@PathVariable UUID goalId) {
    return goalService.getGoal(goalId);
  }

  @PutMapping
  public Goal updateGoal(@RequestBody Goal goal) {
    return goalService.updateGoal(goal);
  }

  @PostMapping("/score")
  @ResponseStatus(HttpStatus.CREATED)
  public void submitGbo(@RequestBody GboSubmission gbo) {
    goalService.recordGoalScore(gbo);
  }
}
