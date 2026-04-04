package uk.ratracejoe.sdq.service;

import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.GboSubmission;
import uk.ratracejoe.sdq.repository.GboRepository;

public class GboServiceImpl implements GboService {
  private final GboRepository gboRepository;

  public GboServiceImpl(GboRepository gboRepository) {
    this.gboRepository = gboRepository;
  }

  @Override
  public void recordResponse(GboSubmission gbo) throws SdqException {
    gboRepository.save(gbo);
  }
}
