package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.UUID;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.GboScore;
import uk.ratracejoe.sdq.repository.GboRepository;

public class GboServiceImpl implements GboService {
  private final GboRepository gboRepository;

  public GboServiceImpl(GboRepository gboRepository) {
    this.gboRepository = gboRepository;
  }

  @Override
  public void recordResponse(UUID fileId, List<GboScore> gbos) throws SdqException {
    gbos.forEach(gboRepository::save);
  }
}
