package uk.ratracejoe.sdq.rest;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.*;
import uk.ratracejoe.sdq.service.GboService;
import uk.ratracejoe.sdq.service.SdqClientService;
import uk.ratracejoe.sdq.service.SdqService;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class SdqClientController {
  private final SdqClientService clientService;
  private final SdqService sdqService;
  private final GboService gboService;

  @GetMapping
  public List<SdqClient> getAll() throws SdqException {
    return clientService.getAll();
  }

  @PostMapping
  public SdqClient create(@RequestBody SdqClient newClient) {
    return clientService.create(newClient);
  }

  @PostMapping("/search")
  public List<SdqClient> getFiltered(@RequestBody Map<DemographicField, String> filters) {
    return clientService.getFiltered(filters);
  }

  @GetMapping("/byId/{clientId}")
  public ResponseEntity<SdqClient> getByUUID(@PathVariable("clientId") UUID uuid)
      throws SdqException {
    return clientService
        .getByUUID(uuid)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/demographic_report/{demographic}")
  public DemographicReport getDemographicReport(
      @PathVariable("demographic") DemographicField demographic) {
    return clientService.getDemographicReport(demographic);
  }

  @GetMapping("/sdq/{clientId}")
  public Map<Assessor, List<SdqScore>> getSdq(@PathVariable("clientId") UUID fileId)
      throws SdqException {
    return sdqService.getScores(fileId);
  }

  @GetMapping("/gbo/{clientId}")
  public Map<Assessor, List<GboScore>> getGbo(@PathVariable("clientId") UUID fileUuid)
      throws SdqException {
    return gboService.getGbo(fileUuid);
  }
}
