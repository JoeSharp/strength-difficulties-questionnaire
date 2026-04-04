package uk.ratracejoe.sdq.rest;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.SdqScore;
import uk.ratracejoe.sdq.service.SdqService;

@RestController
@RequestMapping("/api/sdq")
@RequiredArgsConstructor
public class SdqController {
  private final SdqService sdqService;

  @GetMapping("/{clientId}")
  public Map<Assessor, List<SdqScore>> getSdq(@PathVariable("clientId") UUID fileId)
      throws SdqException {
    return sdqService.getScores(fileId);
  }
}
