package uk.ratracejoe.sdq.repository;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.entity.InterventionTypeEntity;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.tables.InterventionTypeTable;

@RequiredArgsConstructor
public class InterventionTypeRepositoryImpl implements InterventionTypeRepository {
  private final JdbcClient jdbcClient;

  public void save(UUID fileId, String interventionType) {
    jdbcClient
        .sql(InterventionTypeTable.insertOptionSQL())
        .param(1, fileId)
        .param(2, interventionType)
        .update();
  }

  @Override
  public int deleteAll() {
    return jdbcClient.sql(InterventionTypeTable.deleteAllSQL()).update();
  }

  public List<InterventionTypeEntity> getByFile(UUID fileId) throws SdqException {
    return jdbcClient
        .sql(InterventionTypeTable.getByFileSQL())
        .param(1, fileId)
        .query(
            (rs, rowNum) -> {
              String interventionType = rs.getString(InterventionTypeTable.FIELD_INTERVENTION_TYPE);
              return new InterventionTypeEntity(fileId, interventionType);
            })
        .list();
  }
}
