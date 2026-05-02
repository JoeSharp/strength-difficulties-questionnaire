package uk.ratracejoe.sdq.rest;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.ratracejoe.sdq.dto.SdqQueryDTO;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.ReportingPeriod;
import uk.ratracejoe.sdq.model.sdq.SdqSubmission;
import uk.ratracejoe.sdq.model.sdq.SdqSubmissionSummary;
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

  @GetMapping("/{clientId}/reportingPeriods")
  public List<ReportingPeriod> getReportingPeriods(@PathVariable UUID clientId) {
    return sdqService.getReportingPeriods(clientId);
  }

  @GetMapping("/{periodId}/{assessor}")
  public SdqSubmission getSubmission(@PathVariable UUID periodId, @PathVariable Assessor assessor) {
    return sdqService.getSubmission(periodId, assessor);
  }

  @GetMapping("/{periodId}/{assessor}/summary")
  public SdqSubmissionSummary getSubmissionSummary(
      @PathVariable UUID periodId, @PathVariable Assessor assessor) {
    return sdqService.getSubmissionSummary(periodId, assessor);
  }

  @PostMapping("/query")
  public List<SdqSubmissionSummary> getGoalsWithProgress(@RequestBody SdqQueryDTO query) {
    return sdqService.getSdqSummariesWithProgress(
        query.assessor(),
        query.category(),
        query.filters(),
        query.minProgress(),
        query.from(),
        query.to());
  }
}
