package uk.ratracejoe.sdq.rest;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.*;
import uk.ratracejoe.sdq.model.demographics.DemographicField;
import uk.ratracejoe.sdq.model.demographics.DemographicFilter;
import uk.ratracejoe.sdq.model.demographics.DemographicReport;
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
    return clientService.create(newClient);
  }

  @PutMapping
  public SdqClient update(@RequestBody SdqClient client) {
    return clientService.update(client);
  }

  @PostMapping("/search")
  public List<SdqClient> getFiltered(@RequestBody List<DemographicFilter> filters) {
    return clientService.getFiltered(filters);
  }

  @GetMapping("/{clientId}")
  public SdqClient getByUUID(@PathVariable UUID clientId) throws SdqException {
    return clientService.getByUUID(clientId);
  }

  @GetMapping("/demographic_report/{demographic}")
  public DemographicReport getDemographicReport(@PathVariable DemographicField demographic) {
    return clientService.getDemographicReport(demographic);
  }
}
