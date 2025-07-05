package uk.ratracejoe.sdq_analysis.database.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq_analysis.database.entity.DemographicOptionEntity;
import uk.ratracejoe.sdq_analysis.database.entity.InterventionTypeEntity;
import uk.ratracejoe.sdq_analysis.database.tables.DemographicOptionTable;
import uk.ratracejoe.sdq_analysis.dto.DemographicField;
import uk.ratracejoe.sdq_analysis.exception.SdqException;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static uk.ratracejoe.sdq_analysis.database.tables.DemographicOptionTable.FIELD_DEMOGRAPHIC;
import static uk.ratracejoe.sdq_analysis.database.tables.DemographicOptionTable.FIELD_OPTION_TEXT;
import static uk.ratracejoe.sdq_analysis.database.tables.InterventionTypeTable.FIELD_FILE_UUID;
import static uk.ratracejoe.sdq_analysis.database.tables.InterventionTypeTable.FIELD_INTERVENTION_TYPE;

@Service
@RequiredArgsConstructor
public class InterventionTypeRepository extends AbstractRepository {
    private final DataSource dataSource;

    public List<InterventionTypeEntity> getByFile(UUID fileUuid) throws SdqException {
        return handle(dataSource, "getAllDemographicOptions", DemographicOptionTable.getAll(), stmt -> {
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
