package uk.ratracejoe.sdq.service;

import lombok.RequiredArgsConstructor;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.gbo.GboSubmission;
import uk.ratracejoe.sdq.repository.GboRepository;

@RequiredArgsConstructor
public class GboServiceImpl implements GboService {
  private final GboRepository gboRepository;

  @Override
  public void recordResponse(GboSubmission gbo) throws SdqException {
    gboRepository.save(gbo);
  }
}
