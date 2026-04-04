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
import uk.ratracejoe.sdq.model.GboScore;
import uk.ratracejoe.sdq.service.GboService;

@RestController
@RequestMapping("/api/gbo")
@RequiredArgsConstructor
public class GboController {
  private final GboService gboService;

  @GetMapping("/{clientId}")
  public Map<Assessor, List<GboScore>> getGbo(@PathVariable("clientId") UUID fileUuid)
      throws SdqException {
    return gboService.getGbo(fileUuid);
  }
}
