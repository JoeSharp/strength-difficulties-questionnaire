package uk.ratracejoe.sdq.repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import uk.ratracejoe.sdq.entity.InterventionTypeEntity;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.tables.InterventionTypeTable;

@RequiredArgsConstructor
public class InterventionTypeRepositoryImpl implements InterventionTypeRepository {
  private final DataSource dataSource;

  public void save(UUID fileId, String interventionType) {
    RepositoryUtils.handle(
        dataSource,
        "saveInterventionType",
        InterventionTypeTable.insertOptionSQL(),
        stmt -> {
          stmt.setString(1, fileId.toString());
          stmt.setString(2, interventionType);
          return stmt.executeUpdate();
        });
  }

  public List<InterventionTypeEntity> getByFile(UUID fileId) throws SdqException {
    return RepositoryUtils.handle(
        dataSource,
        "getInterventionTypeByFile",
        InterventionTypeTable.getByFileSQL(),
        stmt -> {
          List<InterventionTypeEntity> results = new ArrayList<>();
          stmt.setString(1, fileId.toString());
          ResultSet rs = stmt.executeQuery();

          while (rs.next()) {
            String interventionType = rs.getString(InterventionTypeTable.FIELD_INTERVENTION_TYPE);
            InterventionTypeEntity entity = new InterventionTypeEntity(fileId, interventionType);
            results.add(entity);
          }

          return results;
        });
  }
}
