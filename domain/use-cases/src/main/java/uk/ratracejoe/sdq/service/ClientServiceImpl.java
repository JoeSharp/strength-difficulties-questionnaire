package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.SdqClient;
import uk.ratracejoe.sdq.model.demographics.DemographicField;
import uk.ratracejoe.sdq.model.demographics.DemographicReport;
import uk.ratracejoe.sdq.repository.*;

@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
  private final ClientRepository clientRepository;

  @Override
  public SdqClient create(SdqClient newClient) {
    return clientRepository.createClient(newClient);
  }

  @Override
  public DemographicReport getDemographicReport(DemographicField demographic) {
    return clientRepository.getDemographicReport(demographic);
  }

  @Override
  public List<SdqClient> getAll() throws SdqException {
    return clientRepository.getAll().stream().toList();
  }

  @Override
  public List<SdqClient> getFiltered(Map<DemographicField, String> filters) throws SdqException {
    return clientRepository.getFiltered(filters);
  }

  @Override
  public SdqClient getByUUID(UUID uuid) throws SdqException {
    return clientRepository.get(uuid);
  }

  @Override
  public SdqClient update(SdqClient client) {
    clientRepository.update(client);
    return clientRepository.get(client.clientId());
  }
}
