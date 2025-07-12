package uk.ratracejoe.sdq_analysis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq_analysis.database.entity.GboEntity;
import uk.ratracejoe.sdq_analysis.database.entity.GboPivot;
import uk.ratracejoe.sdq_analysis.database.entity.SdqScoresEntity;
import uk.ratracejoe.sdq_analysis.database.entity.SdqScoresPivot;
import uk.ratracejoe.sdq_analysis.database.repository.GboRepository;
import uk.ratracejoe.sdq_analysis.database.repository.SdqResponseRepository;
import uk.ratracejoe.sdq_analysis.dto.*;
import uk.ratracejoe.sdq_analysis.exception.SdqException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GboService {
    private final DatabaseService dbService;
    private final GboRepository gboRepository;

    public List<GboSummary> getScores(UUID fileUuid) throws SdqException {
        if (!dbService.databaseExists()) throw new SdqException("DB Not ready");

        return gboRepository.getByFileUuid(fileUuid).stream()
                .map(this::toDTO)
                .toList();
    }

    private GboSummary toDTO(GboPivot pivot) {
        return new GboSummary(pivot.uuid(),
                pivot.assessor(),
                pivot.periodIndex(),
                pivot.periodDate(),
                pivot.scores());
    }

    public void recordResponse(ClientFile file,
                               Map<Assessor, List<GboPeriod>> gbosByAssessor) throws SdqException {
        if (!dbService.databaseExists()) throw new SdqException("DB Not ready");

        gbosByAssessor.forEach((assessor, gbos) ->
                gbos.forEach(gbo -> gbo.scores().forEach((key, value) -> {
                    GboEntity entity = new GboEntity(file.uuid(),
                            assessor,
                            gbo.periodIndex(),
                            gbo.periodDate(),
                            key,
                            value);
                    gboRepository.save(entity);
                })));
    }
}
