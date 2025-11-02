package uk.ratracejoe.sdq.service;

import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq.database.entity.SdqPivot;
import uk.ratracejoe.sdq.database.entity.SdqScoresEntity;
import uk.ratracejoe.sdq.database.repository.SdqRepository;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.ClientFile;
import uk.ratracejoe.sdq.model.SdqPeriod;
import uk.ratracejoe.sdq.model.SdqSummary;
import uk.ratracejoe.sdq.repository.DatabaseService;

@Service
@RequiredArgsConstructor
public class SdqServiceImpl implements SdqService {
  private final DatabaseService dbService;
  private final SdqRepository sdqRepository;

  @Override
  public Map<Assessor, List<SdqSummary>> getScores(UUID fileUuid) throws SdqException {
    if (!dbService.databaseExists()) throw new SdqException("DB Not ready");

    return sdqRepository.getScores(fileUuid).stream()
        .collect(
            Collectors.groupingBy(
                SdqPivot::assessor,
                Collectors.collectingAndThen(
                    Collectors.groupingBy(SdqPivot::uuid),
                    fileMap ->
                        fileMap.entrySet().stream()
                            .flatMap(d -> d.getValue().stream())
                            .map(this::toDTO)
                            .toList())));
  }

  private SdqSummary toDTO(SdqPivot pivot) {
    return new SdqSummary(
        pivot.uuid(), pivot.period(), pivot.categoryScores(), pivot.postureScores(), pivot.total());
  }

  public void recordResponse(ClientFile file, List<SdqPeriod> periods) throws SdqException {
    if (!dbService.databaseExists()) throw new SdqException("DB Not ready");

    periods.forEach(
        period ->
            period
                .responses()
                .forEach(
                    (key, value) ->
                        value.forEach(
                            response -> {
                              SdqScoresEntity entity =
                                  new SdqScoresEntity(
                                      file.uuid(),
                                      period.periodIndex(),
                                      key,
                                      response.statement(),
                                      response.score());
                              sdqRepository.recordResponse(entity);
                            })));
  }
}
