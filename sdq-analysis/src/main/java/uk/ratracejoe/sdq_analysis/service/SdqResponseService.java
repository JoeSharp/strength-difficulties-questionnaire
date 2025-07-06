package uk.ratracejoe.sdq_analysis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq_analysis.database.entity.SdqScoresEntity;
import uk.ratracejoe.sdq_analysis.database.entity.SdqScoresPivot;
import uk.ratracejoe.sdq_analysis.database.repository.SdqResponseRepository;
import uk.ratracejoe.sdq_analysis.dto.ClientFile;
import uk.ratracejoe.sdq_analysis.dto.SdqPeriod;
import uk.ratracejoe.sdq_analysis.dto.SdqScoresSummary;
import uk.ratracejoe.sdq_analysis.exception.SdqException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SdqResponseService {
    private final DatabaseService dbService;
    private final SdqResponseRepository sdqResponseRepository;

    public List<SdqScoresSummary> getScores(UUID fileUuid) throws SdqException {
        if (!dbService.databaseExists()) throw new SdqException("DB Not ready");

        return sdqResponseRepository.getScores(fileUuid).stream()
                .map(this::toDTO)
                .toList();
    }

    private SdqScoresSummary toDTO(SdqScoresPivot pivot) {
        return new SdqScoresSummary(pivot.uuid(),
                pivot.period(),
                pivot.assessor(),
                pivot.categoryScores(),
                pivot.postureScores(),
                pivot.total());
    }

    public void recordResponse(ClientFile file,
                               List<SdqPeriod> periods) throws SdqException {
        if (!dbService.databaseExists()) throw new SdqException("DB Not ready");

        periods.forEach(period ->
                period.responses().forEach((key, value) -> value.forEach(response -> {
                    SdqScoresEntity entity = new SdqScoresEntity(file.uuid(),
                            period.periodIndex(),
                            key,
                            response.statement(),
                            response.score());
                    sdqResponseRepository.recordResponse(entity);
                })));
    }
}
