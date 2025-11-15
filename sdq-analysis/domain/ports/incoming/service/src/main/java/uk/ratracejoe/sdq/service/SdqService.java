package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.*;

public interface SdqService {
  Map<Assessor, List<SdqScore>> getScores(UUID fileId) throws SdqException;

  void recordResponse(UUID fileId, List<SdqPeriod> periods) throws SdqException;
}
