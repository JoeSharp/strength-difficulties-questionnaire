package uk.ratracejoe.sdq.service;

import static java.util.stream.Collectors.groupingBy;

import java.util.*;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.*;
import uk.ratracejoe.sdq.repository.SdqRepository;

public class SdqServiceImpl implements SdqService {
  private final SdqRepository sdqRepository;

  public SdqServiceImpl(SdqRepository sdqRepository) {
    this.sdqRepository = sdqRepository;
  }

  @Override
  public Map<Assessor, List<SdqScore>> getScores(UUID fileId) throws SdqException {
    List<SdqScore> scores = sdqRepository.getScores(fileId);

    return scores.stream().collect(groupingBy(SdqScore::assessor));
  }

  public void recordResponse(UUID fileId, List<SdqPeriod> periods) throws SdqException {
    periods.forEach(
        period ->
            period
                .responses()
                .forEach(
                    (key, value) ->
                        value.forEach(
                            response -> {
                              SdqScore entity =
                                  new SdqScore(
                                      fileId,
                                      period.periodIndex(),
                                      key,
                                      response.statement(),
                                      response.score());
                              sdqRepository.recordResponse(entity);
                            })));
  }
}
