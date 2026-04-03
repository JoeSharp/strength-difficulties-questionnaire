package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.DemographicField;
import uk.ratracejoe.sdq.model.DemographicReport;
import uk.ratracejoe.sdq.model.SdqClient;
import uk.ratracejoe.sdq.repository.*;

public class SdqClientServiceImpl implements SdqClientService {
  private final SdqClientRepository clientRepository;

  public SdqClientServiceImpl(SdqClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

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
  public Optional<SdqClient> getByUUID(UUID uuid) throws SdqException {
    return clientRepository.getByUUID(uuid);
  }
}
