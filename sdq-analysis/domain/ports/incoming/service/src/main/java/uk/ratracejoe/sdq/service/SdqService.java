package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.SdqSummary;

public interface SdqService {
  Map<Assessor, List<SdqSummary>> getScores(UUID fileUuid) throws SdqException;
}
