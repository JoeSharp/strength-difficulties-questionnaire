package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.UUID;
import uk.ratracejoe.sdq.model.GboScore;

public interface GboService {

  void recordResponse(UUID fileId, List<GboScore> gbos);
}
