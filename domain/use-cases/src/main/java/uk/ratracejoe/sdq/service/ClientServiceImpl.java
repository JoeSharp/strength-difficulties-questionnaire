package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.SdqClient;
import uk.ratracejoe.sdq.model.demographics.DemographicField;
import uk.ratracejoe.sdq.model.demographics.DemographicReport;
import uk.ratracejoe.sdq.model.demographics.InterventionType;
import uk.ratracejoe.sdq.repository.*;

@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
  private final ClientRepository clientRepository;
  private final InterventionTypeRepository interventionTypeRepository;

  @Override
  public SdqClient create(SdqClient newClient) {
    SdqClient client = clientRepository.createClient(newClient);
    newClient
        .interventionTypes()
        .forEach(it -> interventionTypeRepository.save(client.clientId(), it));
    return getByUUID(client.clientId());
  }

  @Override
  public DemographicReport getDemographicReport(DemographicField demographic) {
    return clientRepository.getDemographicReport(demographic);
  }

  @Override
  public List<SdqClient> getAll() throws SdqException {
    return clientRepository.getAll().stream().map(this::withInterventionTypes).toList();
  }

  @Override
  public List<SdqClient> getFiltered(Map<DemographicField, String> filters) throws SdqException {
    return clientRepository.getFiltered(filters).stream().map(this::withInterventionTypes).toList();
  }

  @Override
  public SdqClient getByUUID(UUID clientId) throws SdqException {
    return withInterventionTypes(clientRepository.get(clientId));
  }

  @Override
  public SdqClient update(SdqClient client) {
    clientRepository.update(client);
    interventionTypeRepository.deleteForClient(client.clientId());
    client
        .interventionTypes()
        .forEach(it -> interventionTypeRepository.save(client.clientId(), it));
    return withInterventionTypes(clientRepository.get(client.clientId()));
  }

  private SdqClient withInterventionTypes(SdqClient client) {
    List<InterventionType> interventionTypes =
        interventionTypeRepository.getForClient(client.clientId());
    return client.withInterventionTypes(interventionTypes);
  }
}
