package uk.ratracejoe.sdq_analysis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq_analysis.database.entity.ClientFileEntity;
import uk.ratracejoe.sdq_analysis.database.entity.InterventionTypeEntity;
import uk.ratracejoe.sdq_analysis.database.entity.SdqScoresPivot;
import uk.ratracejoe.sdq_analysis.database.repository.ClientFileRepository;
import uk.ratracejoe.sdq_analysis.database.repository.InterventionTypeRepository;
import uk.ratracejoe.sdq_analysis.database.repository.SdqResponseRepository;
import uk.ratracejoe.sdq_analysis.dto.ClientFile;
import uk.ratracejoe.sdq_analysis.dto.SdqScoresSummary;
import uk.ratracejoe.sdq_analysis.exception.SdqException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientFileService {
    private final ClientFileRepository fileRepository;
    private final InterventionTypeRepository interventionTypeRepository;

    public List<ClientFile> getAll() throws SdqException {
        return fileRepository
                .getAll()
                .stream().map(this::toDTO)
                .toList();
    }

    private ClientFile toDTO(ClientFileEntity entity) {
        List<String> interventionTypes =
                interventionTypeRepository.getByFile(entity.uuid())
                        .stream().map(InterventionTypeEntity::interventionType)
                        .toList();
        return new ClientFile(entity.uuid(),
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

    public Optional<ClientFile> getByUUID(UUID uuid) throws SdqException {
        return fileRepository.getByUUID(uuid)
                .map(this::toDTO);
    }
}
