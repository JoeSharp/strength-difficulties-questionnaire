package uk.ratracejoe.sdq.repository;

import java.util.UUID;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.sdq.SdqSubmission;

public interface SdqRepository {

  void save(SdqSubmission sdq) throws SdqException;

  SdqSubmission get(UUID periodId, Assessor assessor);

  int deleteAll();
}
