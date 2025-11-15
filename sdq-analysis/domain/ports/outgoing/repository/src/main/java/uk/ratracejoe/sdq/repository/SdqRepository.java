package uk.ratracejoe.sdq.repository;

import java.util.List;
import java.util.UUID;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.SdqScore;

public interface SdqRepository {
  List<SdqScore> getScores(UUID fileId) throws SdqException;

  void recordResponse(SdqScore score) throws SdqException;
}
