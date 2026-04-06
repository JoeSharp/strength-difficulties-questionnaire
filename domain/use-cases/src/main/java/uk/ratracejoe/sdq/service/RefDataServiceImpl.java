package uk.ratracejoe.sdq.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.demographics.*;

public class RefDataServiceImpl implements RefDataService {

  public RefDataServiceImpl() {}

  @Override
  public Map<DemographicField, List<String>> getDemographicOptions() throws SdqException {
    return Map.of(
        DemographicField.Gender,
        Arrays.stream(Gender.values()).map(Gender::name).toList(),
        DemographicField.Council,
        Arrays.stream(Council.values()).map(Council::name).toList(),
        DemographicField.Ethnicity,
        Arrays.stream(Ethnicity.values()).map(Ethnicity::name).toList(),
        DemographicField.EAL,
        Arrays.stream(EnglishAsAdditionalLanguage.values())
            .map(EnglishAsAdditionalLanguage::name)
            .toList(),
        DemographicField.DisabilityStatus,
        Arrays.stream(DisabilityStatus.values()).map(DisabilityStatus::name).toList(),
        DemographicField.DisabilityType,
        Arrays.stream(DisabilityType.values()).map(DisabilityType::name).toList(),
        DemographicField.CareExperience,
        Arrays.stream(CareExperience.values()).map(CareExperience::name).toList(),
        DemographicField.InterventionType,
        Arrays.stream(InterventionType.values()).map(InterventionType::name).toList(),
        DemographicField.FundingSource,
        Arrays.stream(FundingSource.values()).map(FundingSource::name).toList());
  }
}
