package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.Map;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.DemographicField;
import uk.ratracejoe.sdq.repository.DemographicOptionRepository;

public class RefDataServiceImpl implements RefDataService {
  private final DemographicOptionRepository demographicOptionRepository;

  public RefDataServiceImpl(DemographicOptionRepository demographicOptionRepository) {
    this.demographicOptionRepository = demographicOptionRepository;
  }

  @Override
  public Map<DemographicField, List<String>> getDemographicOptions() throws SdqException {
    return demographicOptionRepository.getOptionsByField();
  }
}
