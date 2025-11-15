package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.Map;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.DemographicField;

public interface RefDataService {
  Map<DemographicField, List<String>> getDemographicOptions() throws SdqException;
}
