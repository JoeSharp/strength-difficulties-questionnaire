package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.GboPeriod;
import uk.ratracejoe.sdq.model.GboScore;
import uk.ratracejoe.sdq.repository.GboRepository;

public class GboServiceImpl implements GboService {
  private final GboRepository gboRepository;

  public GboServiceImpl(GboRepository gboRepository) {
    this.gboRepository = gboRepository;
  }

  @Override
  public Map<Assessor, List<GboScore>> getGbo(UUID fileUuid) throws SdqException {
    return gboRepository.getByFileUuid(fileUuid).stream()
        .collect(
            Collectors.groupingBy(
                GboScore::assessor,
                Collectors.collectingAndThen(
                    Collectors.groupingBy(GboScore::fileId),
                    fileMap ->
                        fileMap.entrySet().stream().flatMap(d -> d.getValue().stream()).toList())));
  }

  @Override
  public void recordResponse(UUID fileId, Map<Assessor, List<GboPeriod>> gbosByAssessor)
      throws SdqException {
    gbosByAssessor.forEach(
        (assessor, gbos) ->
            gbos.forEach(
                gbo ->
                    gbo.scores()
                        .forEach(
                            (key, value) -> {
                              GboScore entity =
                                  new GboScore(
                                      fileId,
                                      assessor,
                                      gbo.periodIndex(),
                                      gbo.periodDate(),
                                      key,
                                      value);
                              gboRepository.save(entity);
                            })));
  }
}
