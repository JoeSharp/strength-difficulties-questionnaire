package uk.ratracejoe.sdq_analysis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq_analysis.database.entity.ClientFileEntity;
import uk.ratracejoe.sdq_analysis.database.entity.SdqScoresPivot;
import uk.ratracejoe.sdq_analysis.database.repository.ClientFileRepository;
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
    private final SdqResponseRepository sdqResponseRepository;

    public List<ClientFile> getAll() throws SdqException {
        List<ClientFileEntity> fileEntities = fileRepository
                .getAll();

        return Collections.emptyList();
    }

    public List<SdqScoresSummary> getScores() throws SdqException {
        List<SdqScoresPivot> scoresPivots = sdqResponseRepository.getScores();

        return Collections.emptyList();
    }

    public Optional<ClientFile> getByUUID(UUID uuid) throws SdqException {
        Optional<ClientFileEntity> fileEntity = fileRepository
                .getByUUID(uuid);
        return Optional.empty();
    }
}
