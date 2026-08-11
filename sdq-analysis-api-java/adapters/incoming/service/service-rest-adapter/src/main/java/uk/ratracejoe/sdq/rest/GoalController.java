package uk.ratracejoe.sdq.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.ratracejoe.sdq.dto.GoalQueryDTO;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.gbo.GboSubmission;
import uk.ratracejoe.sdq.model.gbo.Goal;
import uk.ratracejoe.sdq.model.gbo.GoalProgress;
import uk.ratracejoe.sdq.service.GoalService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

  @PostMapping("/query")
  public List<GoalProgress> getGoalsWithProgress(@RequestBody GoalQueryDTO query) {
    return goalService.getGoalsWithProgress(
        query.assessors(),
        Optional.ofNullable(query.filters()).orElseGet(Collections::emptyList),
        query.minProgress(),
        Optional.ofNullable(query.goalTypes()).orElseGet(Collections::emptyList),
        query.from(),
        query.to());
  }

  @GetMapping("/forClient/{clientId}")
  public List<Goal> getGoalsForClient(@PathVariable UUID clientId) {
    return goalService.getGoalsForClient(clientId);
  }

  @GetMapping("/forClient/{clientId}/progress/{assessor}")
  public List<GoalProgress> getGoalsProgressForClient(
      @PathVariable UUID clientId, @PathVariable Assessor assessor) {
    return goalService.getGoalsProgressForClient(clientId, assessor);
  }

  @GetMapping("/{goalId}")
  public Goal getGoal(@PathVariable UUID goalId) {
    return goalService.getGoal(goalId);
  }

  @GetMapping("/{goalId}/progress/{assessor}")
  public GoalProgress getGoalProgress(@PathVariable UUID goalId, @PathVariable Assessor assessor) {
    return goalService.getGoalProgress(goalId, assessor);
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
