package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.SdqClient;
import uk.ratracejoe.sdq.model.demographics.*;
import uk.ratracejoe.sdq.repository.AcesRepository;
import uk.ratracejoe.sdq.repository.ClientRepository;
import uk.ratracejoe.sdq.repository.DisabilityTypeRepository;
import uk.ratracejoe.sdq.repository.InterventionRepository;

@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
  private final ClientRepository clientRepository;
  private final InterventionRepository interventionRepository;
  private final DisabilityTypeRepository disabilityTypeRepository;
  private final AcesRepository acesRepository;

  @Override
  public SdqClient create(SdqClient newClient) {
    SdqClient client = clientRepository.createClient(newClient);
    Optional.ofNullable(newClient.interventions())
        .ifPresent(its -> its.forEach(it -> interventionRepository.save(client.clientId(), it)));
    Optional.ofNullable(newClient.disabilityTypes())
        .ifPresent(dts -> dts.forEach(dt -> disabilityTypeRepository.save(client.clientId(), dt)));
    Optional.ofNullable(newClient.aces())
        .ifPresent(
            aces ->
                aces.forEach((key, value) -> acesRepository.save(client.clientId(), key, value)));
    return getByUUID(client.clientId());
  }

  @Override
  public DemographicReport getDemographicReport(DemographicField demographic) {
    return clientRepository.getDemographicReport(demographic);
  }

  @Override
  public List<SdqClient> getAll() throws SdqException {
    return clientRepository.getAll().stream().map(this::enriched).toList();
  }

  @Override
  public List<SdqClient> getFiltered(String partialName, List<DemographicFilter> filters)
      throws SdqException {
    return clientRepository.getFiltered(partialName, filters).stream().map(this::enriched).toList();
  }

  @Override
  public SdqClient getByUUID(UUID clientId) throws SdqException {
    return enriched(clientRepository.get(clientId));
  }

  @Override
  public SdqClient update(SdqClient client) {
    clientRepository.update(client);
    interventionRepository.deleteForClient(client.clientId());
    disabilityTypeRepository.deleteForClient(client.clientId());
    acesRepository.deleteForClient(client.clientId());
    client.interventions().forEach(it -> interventionRepository.save(client.clientId(), it));
    client.disabilityTypes().forEach(dt -> disabilityTypeRepository.save(client.clientId(), dt));
    client.aces().forEach((key, value) -> acesRepository.save(client.clientId(), key, value));
    return enriched(clientRepository.get(client.clientId()));
  }

  @Override
  public int deleteAllClients() {
    return clientRepository.deleteAll();
  }

  @Override
  public int deleteByClientId(UUID clientId) {
    return clientRepository.deleteByClientId(clientId);
  }

  private SdqClient enriched(SdqClient client) {
    List<Intervention> interventions = interventionRepository.getForClient(client.clientId());
    List<DisabilityType> disabilityTypes = disabilityTypeRepository.getForClient(client.clientId());
    Map<AceType, Integer> aces = acesRepository.getForClient(client.clientId());
    return client
        .withInterventions(interventions)
        .withDisabilityTypes(disabilityTypes)
        .withAces(aces);
  }
}
