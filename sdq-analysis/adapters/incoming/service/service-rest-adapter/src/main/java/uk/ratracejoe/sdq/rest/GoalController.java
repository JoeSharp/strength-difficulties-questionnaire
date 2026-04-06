package uk.ratracejoe.sdq.rest;

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

  @PostMapping("/score")
  @ResponseStatus(HttpStatus.CREATED)
  public void submitGbo(@RequestBody GboSubmission gbo) {
    goalService.recordGoalScore(gbo);
  }
}
