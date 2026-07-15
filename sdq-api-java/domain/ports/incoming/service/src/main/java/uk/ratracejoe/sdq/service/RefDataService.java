package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.Map;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.EnumValue;
import uk.ratracejoe.sdq.model.demographics.DemographicField;
import uk.ratracejoe.sdq.model.sdq.Category;
import uk.ratracejoe.sdq.model.sdq.Statement;

public interface RefDataService {
  Map<DemographicField, List<EnumValue>> getDemographicOptions() throws SdqException;

  List<Statement> getStatements();

  List<Category> getCategories();

  Statement getStatement(String key);

  List<EnumValue> getGoalTypes();
}
