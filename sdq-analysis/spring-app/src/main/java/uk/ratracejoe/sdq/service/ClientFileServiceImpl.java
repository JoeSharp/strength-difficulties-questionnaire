package uk.ratracejoe.sdq.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq.entity.ClientFileEntity;
import uk.ratracejoe.sdq.entity.InterventionTypeEntity;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.ClientFile;
import uk.ratracejoe.sdq.model.DemographicReport;
import uk.ratracejoe.sdq.repository.ClientFileRepository;
import uk.ratracejoe.sdq.repository.DatabaseService;
import uk.ratracejoe.sdq.repository.InterventionTypeRepository;

@Service
@RequiredArgsConstructor
public class ClientFileServiceImpl implements ClientFileService {
  private final DatabaseService dbService;
  private final ClientFileRepository fileRepository;
  private final InterventionTypeRepository interventionTypeRepository;

  @Override
  public DemographicReport getDemographicReport(String demographic) {
    return fileRepository.getDemographicReport(demographic);
  }

  @Override
  public List<ClientFile> getAll() throws SdqException {
    if (!dbService.databaseExists()) return Collections.emptyList();

    return fileRepository.getAll().stream().map(this::toDTO).toList();
  }

  private ClientFile toDTO(ClientFileEntity entity) {
    List<String> interventionTypes =
        interventionTypeRepository.getByFile(entity.uuid()).stream()
            .map(InterventionTypeEntity::interventionType)
            .toList();
    return new ClientFile(
        entity.uuid(),
        entity.filename(),
        entity.dateOfBirth(),
        entity.gender(),
        entity.council(),
        entity.ethnicity(),
        entity.englishAdditionalLanguage(),
        entity.disabilityStatus(),
        entity.disabilityType(),
        entity.careExperience(),
        interventionTypes,
        entity.aces(),
        entity.fundingSource());
  }

  @Override
  public Optional<ClientFile> getByUUID(UUID uuid) throws SdqException {
    if (!dbService.databaseExists()) return Optional.empty();

    return fileRepository.getByUUID(uuid).map(this::toDTO);
  }
}
