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

  public void recordResponse(UUID fileId, List<SdqScore> sdqs) throws SdqException {
    sdqs.forEach(sdqRepository::recordResponse);
  }
}
