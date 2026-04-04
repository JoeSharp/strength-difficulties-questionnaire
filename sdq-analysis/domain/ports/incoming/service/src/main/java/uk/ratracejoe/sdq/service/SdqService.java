package uk.ratracejoe.sdq.service;

import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.*;

public interface SdqService {

  void recordResponse(SdqSubmission sdq) throws SdqException;
}
