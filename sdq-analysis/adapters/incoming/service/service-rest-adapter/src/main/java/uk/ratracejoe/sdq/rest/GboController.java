package uk.ratracejoe.sdq.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.ratracejoe.sdq.model.GboSubmission;
import uk.ratracejoe.sdq.service.GboService;

@RestController
@RequestMapping("/api/gbo")
@RequiredArgsConstructor
public class GboController {
  private final GboService gboService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  void submitGbo(@RequestBody GboSubmission gbo) {
    gboService.recordResponse(gbo);
  }
}
