package uk.ratracejoe.sdq_analysis.database.repository;

import static uk.ratracejoe.sdq_analysis.database.repository.RepositoryUtils.handle;
import static uk.ratracejoe.sdq_analysis.database.tables.DemographicOptionTable.FIELD_DEMOGRAPHIC;
import static uk.ratracejoe.sdq_analysis.database.tables.DemographicOptionTable.FIELD_OPTION_TEXT;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq_analysis.database.entity.DemographicOptionEntity;
import uk.ratracejoe.sdq_analysis.database.tables.DemographicOptionTable;
import uk.ratracejoe.sdq_analysis.dto.DemographicField;
import uk.ratracejoe.sdq_analysis.exception.SdqException;

@Service
@RequiredArgsConstructor
public class DemographicOptionRepository {
  private final DataSource dataSource;

  public Map<DemographicField, List<String>> getOptionsByField() throws SdqException {
    return getAll().stream()
        .collect(
            Collectors.toMap(
                DemographicOptionEntity::field,
                k -> List.of(k.optionText()),
                (a, b) -> Stream.concat(a.stream(), b.stream()).toList()));
  }

  public List<DemographicOptionEntity> getAll() throws SdqException {
    return handle(
        dataSource,
        "getAllDemographicOptions",
        DemographicOptionTable.getAll(),
        stmt -> {
          List<DemographicOptionEntity> results = new ArrayList<>();
          ResultSet rs = stmt.executeQuery();

          while (rs.next()) {
            DemographicField field = DemographicField.valueOf(rs.getString(FIELD_DEMOGRAPHIC));
            String optionText = rs.getString(FIELD_OPTION_TEXT);
            DemographicOptionEntity entity = new DemographicOptionEntity(field, optionText);
            results.add(entity);
          }

          return results;
        });
  }
}
