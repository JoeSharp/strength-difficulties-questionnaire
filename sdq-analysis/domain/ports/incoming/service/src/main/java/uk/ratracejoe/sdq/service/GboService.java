package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.ClientFile;
import uk.ratracejoe.sdq.model.GboPeriod;
import uk.ratracejoe.sdq.model.GboSummary;

public interface GboService {
  Map<Assessor, List<GboSummary>> getGbo(UUID fileUuid) throws SdqException;

  void recordResponse(ClientFile file, Map<Assessor, List<GboPeriod>> gbosByAssessor);
}
