package uk.ratracejoe.sdq.database.repository;

import static uk.ratracejoe.sdq.database.repository.RepositoryUtils.handle;
import static uk.ratracejoe.sdq.database.tables.InterventionTypeTable.FIELD_INTERVENTION_TYPE;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq.database.entity.InterventionTypeEntity;
import uk.ratracejoe.sdq.database.tables.InterventionTypeTable;
import uk.ratracejoe.sdq.exception.SdqException;

@Service
@RequiredArgsConstructor
public class InterventionTypeRepository {
  private final DataSource dataSource;

  public void save(InterventionTypeEntity interventionType) {
    handle(
        dataSource,
        "saveInterventionType",
        InterventionTypeTable.insertOptionSQL(),
        stmt -> {
          stmt.setString(1, interventionType.fileUuid().toString());
          stmt.setString(2, interventionType.interventionType());
          return stmt.executeUpdate();
        });
  }

  public List<InterventionTypeEntity> getByFile(UUID fileUuid) throws SdqException {
    return handle(
        dataSource,
        "getInterventionTypeByFile",
        InterventionTypeTable.getByFileSQL(),
        stmt -> {
          List<InterventionTypeEntity> results = new ArrayList<>();
          stmt.setString(1, fileUuid.toString());
          ResultSet rs = stmt.executeQuery();

          while (rs.next()) {
            String interventionType = rs.getString(FIELD_INTERVENTION_TYPE);
            InterventionTypeEntity entity = new InterventionTypeEntity(fileUuid, interventionType);
            results.add(entity);
          }

          return results;
        });
  }
}
