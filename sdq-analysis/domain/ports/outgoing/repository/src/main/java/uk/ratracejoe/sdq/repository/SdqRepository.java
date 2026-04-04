package uk.ratracejoe.sdq.repository;

import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.SdqSubmission;

public interface SdqRepository {

  void recordResponse(SdqSubmission sdq) throws SdqException;

  int deleteAll();
}
