package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.UUID;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.*;

public interface SdqService {

  void recordResponse(UUID fileId, List<SdqScore> sdqs) throws SdqException;
}
