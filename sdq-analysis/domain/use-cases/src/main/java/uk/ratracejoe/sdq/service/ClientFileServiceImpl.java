package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.ClientFile;
import uk.ratracejoe.sdq.model.DemographicReport;
import uk.ratracejoe.sdq.repository.*;

public class ClientFileServiceImpl implements ClientFileService {
  private final ClientFileRepository fileRepository;
  private final InterventionTypeRepository interventionTypeRepository;

  public ClientFileServiceImpl(
      ClientFileRepository fileRepository, InterventionTypeRepository interventionTypeRepository) {
    this.fileRepository = fileRepository;
    this.interventionTypeRepository = interventionTypeRepository;
  }

  @Override
  public DemographicReport getDemographicReport(String demographic) {
    return fileRepository.getDemographicReport(demographic);
  }

  @Override
  public List<ClientFile> getAll() throws SdqException {
    return fileRepository.getAll().stream().toList();
  }

  @Override
  public Optional<ClientFile> getByUUID(UUID uuid) throws SdqException {
    return fileRepository.getByUUID(uuid);
  }
}
