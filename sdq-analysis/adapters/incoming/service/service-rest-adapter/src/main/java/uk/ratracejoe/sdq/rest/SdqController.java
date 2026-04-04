package uk.ratracejoe.sdq.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.ratracejoe.sdq.model.SdqSubmission;
import uk.ratracejoe.sdq.service.SdqService;

@RestController
@RequestMapping("/api/sdq")
@RequiredArgsConstructor
public class SdqController {
  private final SdqService sdqService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  void submitSdq(@RequestBody SdqSubmission sdq) {
    sdqService.recordResponse(sdq);
  }
}
