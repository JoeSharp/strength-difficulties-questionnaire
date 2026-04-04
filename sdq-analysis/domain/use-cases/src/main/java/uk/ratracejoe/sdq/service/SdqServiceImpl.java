package uk.ratracejoe.sdq.service;

import lombok.RequiredArgsConstructor;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.*;
import uk.ratracejoe.sdq.repository.SdqRepository;

@RequiredArgsConstructor
public class SdqServiceImpl implements SdqService {
  private final SdqRepository sdqRepository;

  public void recordResponse(SdqSubmission sdq) throws SdqException {
    sdqRepository.save(sdq);
  }
}
