package uk.ratracejoe.sdq.repository;

import static uk.ratracejoe.sdq.tables.DemographicOptionTable.FIELD_DEMOGRAPHIC;
import static uk.ratracejoe.sdq.tables.DemographicOptionTable.FIELD_OPTION_TEXT;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.entity.DemographicOptionEntity;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.DemographicField;
import uk.ratracejoe.sdq.model.SdqEnumerations;
import uk.ratracejoe.sdq.tables.DemographicOptionTable;

@RequiredArgsConstructor
public class DemographicOptionRepositoryImpl implements DemographicOptionRepository {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(DemographicOptionRepositoryImpl.class);
  private final JdbcClient jdbcClient;

  @Override
  public void ensureEnumerations(SdqEnumerations sdqEnumerations) {
    createDemographicOptions(sdqEnumerations.demographics());
  }

  private void createDemographicOptions(Map<DemographicField, List<String>> demographics) {
    demographics.forEach(
        (key, value) ->
            value.forEach(
                optionValue -> {
                  try {
                    jdbcClient
                        .sql(DemographicOptionTable.insertOptionSQL())
                        .param(1, key.name())
                        .param(2, optionValue)
                        .update();
                  } catch (DataAccessException e) {
                    LOGGER.error(e.getMessage());
                  }
                }));
  }

  public Map<DemographicField, List<String>> getOptionsByField() throws SdqException {
    return getAll().stream()
        .collect(
            Collectors.toMap(
                DemographicOptionEntity::field,
                k -> List.of(k.optionText()),
                (a, b) -> Stream.concat(a.stream(), b.stream()).toList()));
  }

  @Override
  public int deleteAll() {
    return jdbcClient.sql(DemographicOptionTable.deleteAllSQL()).update();
  }

  public List<DemographicOptionEntity> getAll() {
    String sql = DemographicOptionTable.getAll();

    return jdbcClient
        .sql(sql)
        .query(
            (rs, rowNum) ->
                new DemographicOptionEntity(
                    DemographicField.valueOf(rs.getString(FIELD_DEMOGRAPHIC)),
                    rs.getString(FIELD_OPTION_TEXT)))
        .list();
  }
}
