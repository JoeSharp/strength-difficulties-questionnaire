package uk.ratracejoe.sdq.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.demographics.*;
import uk.ratracejoe.sdq.model.sdq.Category;
import uk.ratracejoe.sdq.model.sdq.Statement;
import uk.ratracejoe.sdq.repository.StatementRepository;

@RequiredArgsConstructor
public class RefDataServiceImpl implements RefDataService {
  private List<Category> categories;
  private List<Statement> statements;
  private Map<String, Statement> byKey;

  private final StatementRepository repository;

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

  @Override
  public List<Statement> getStatements() {
    ensure();
    return statements;
  }

  @Override
  public List<Category> getCategories() {
    ensure();
    return categories;
  }

  @Override
  public Statement getStatement(String key) {
    ensure();
    return byKey.get(key);
  }

  private void ensure() {
    if (Objects.nonNull(statements)) return;

    categories = repository.getCategories();
    statements = repository.getStatements();
    byKey = statements.stream().collect(Collectors.toMap(Statement::key, Function.identity()));
  }
}
