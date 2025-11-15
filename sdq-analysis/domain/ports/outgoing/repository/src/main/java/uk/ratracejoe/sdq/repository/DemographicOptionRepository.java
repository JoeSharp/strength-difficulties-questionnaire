package uk.ratracejoe.sdq.repository;

import java.util.List;
import java.util.Map;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.DemographicField;
import uk.ratracejoe.sdq.model.SdqEnumerations;

public interface DemographicOptionRepository {
  void ensureEnumerations(SdqEnumerations sdqEnumerations);

  Map<DemographicField, List<String>> getOptionsByField() throws SdqException;
}
