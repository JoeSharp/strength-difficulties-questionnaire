package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.ClientFile;
import uk.ratracejoe.sdq.model.DemographicField;
import uk.ratracejoe.sdq.model.DemographicReport;
import uk.ratracejoe.sdq.repository.*;

public class ClientFileServiceImpl implements ClientFileService {
  private final ClientFileRepository fileRepository;

  public ClientFileServiceImpl(ClientFileRepository fileRepository) {
    this.fileRepository = fileRepository;
  }

  @Override
  public DemographicReport getDemographicReport(DemographicField demographic) {
    return fileRepository.getDemographicReport(demographic);
  }

  @Override
  public List<ClientFile> getAll() throws SdqException {
    return fileRepository.getAll().stream().toList();
  }

  @Override
  public List<ClientFile> getFiltered(Map<DemographicField, String> filters) throws SdqException {
    return fileRepository.getFiltered(filters);
  }

  @Override
  public Optional<ClientFile> getByUUID(UUID uuid) throws SdqException {
    return fileRepository.getByUUID(uuid);
  }
}
