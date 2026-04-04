package uk.ratracejoe.sdq.service;

import java.util.*;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.*;
import uk.ratracejoe.sdq.repository.SdqRepository;

public class SdqServiceImpl implements SdqService {
  private final SdqRepository sdqRepository;

  public SdqServiceImpl(SdqRepository sdqRepository) {
    this.sdqRepository = sdqRepository;
  }

  public void recordResponse(UUID fileId, List<SdqScore> sdqs) throws SdqException {
    sdqs.forEach(sdqRepository::recordResponse);
  }
}
