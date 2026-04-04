package uk.ratracejoe.sdq.rest;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.*;
import uk.ratracejoe.sdq.service.ClientService;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {
  private final ClientService clientService;

  @GetMapping
  public List<SdqClient> getAll() throws SdqException {
    return clientService.getAll();
  }

  @PostMapping
  public SdqClient create(@RequestBody SdqClient newClient) {
    SdqClient created = clientService.create(newClient);
    return created;
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
}
