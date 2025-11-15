package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.GboPeriod;
import uk.ratracejoe.sdq.model.GboScore;

public interface GboService {
  Map<Assessor, List<GboScore>> getGbo(UUID fileUuid) throws SdqException;

  void recordResponse(UUID fileId, Map<Assessor, List<GboPeriod>> gbosByAssessor);
}
