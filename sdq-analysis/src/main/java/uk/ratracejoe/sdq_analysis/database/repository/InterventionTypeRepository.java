package uk.ratracejoe.sdq_analysis.database.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq_analysis.database.entity.InterventionTypeEntity;
import uk.ratracejoe.sdq_analysis.database.tables.InterventionTypeTable;
import uk.ratracejoe.sdq_analysis.exception.SdqException;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static uk.ratracejoe.sdq_analysis.database.repository.RepositoryUtils.handle;
import static uk.ratracejoe.sdq_analysis.database.tables.InterventionTypeTable.FIELD_INTERVENTION_TYPE;

@Service
@RequiredArgsConstructor
public class InterventionTypeRepository {
    private final DataSource dataSource;

    public void save(InterventionTypeEntity interventionType) {
        handle(dataSource, "saveInterventionType", InterventionTypeTable.insertOptionSQL(), stmt -> {
            stmt.setString(1, interventionType.fileUuid().toString());
            stmt.setString(2, interventionType.interventionType());
            return stmt.executeUpdate();
        });
    }

    public List<InterventionTypeEntity> getByFile(UUID fileUuid) throws SdqException {
        return handle(dataSource, "getInterventionTypeByFile", InterventionTypeTable.getByFileSQL(), stmt -> {
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
