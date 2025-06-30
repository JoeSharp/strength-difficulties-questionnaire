package uk.ratracejoe.sdq_analysis.repository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq_analysis.dto.*;
import uk.ratracejoe.sdq_analysis.exception.SdqException;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static uk.ratracejoe.sdq_analysis.repository.AbstractRepository.handle;
import static uk.ratracejoe.sdq_analysis.repository.UploadFileTable.*;

@Service
@RequiredArgsConstructor
public class UploadFileRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadFileRepository.class);
    private final DataSource dataSource;

    public void saveFile(UploadFile file) throws SdqException {
        handle(dataSource, "saveFile", UploadFileTable.insertSQL(), stmt -> {
            stmt.setString(1, file.uuid().toString());
            stmt.setString(2, file.filename());
            stmt.setString(3, file.dateOfBirth());
            stmt.setString(4, file.gender().name());
            stmt.setString(5, file.ethnicity().name());
            stmt.setString(6, file.englishAdditionalLanguage().name());
            stmt.setString(7, file.disabilityStatus().name());
            stmt.setString(8, file.disabilityType().name());
            stmt.setString(9, file.careExperience().name());
            stmt.setString(10, file.interventionType().name());
            stmt.setString(11, file.aces().name());
            stmt.setString(12, file.fundingSource().name());
            int rowsUpdated = stmt.executeUpdate();
            LOGGER.info("Inserted File to database, rows updated {}", rowsUpdated);
            return rowsUpdated;
        });
    }

    public Optional<UploadFile> getByUUID(UUID uuid) throws SdqException {
        return handle(dataSource, "getByUUID", UploadFileTable.selectByUUID(), stmt -> {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) return Optional.empty();
            return Optional.of(getFromResultSet(rs));
        });
    }

    public List<UploadFile> getAll() throws SdqException {
        return handle(dataSource, "getAll", UploadFileTable.selectAllSQL(), stmt -> {
            List<UploadFile> files = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                files.add(getFromResultSet(rs));
            }
            return files;
        });
    }

    private UploadFile getFromResultSet(ResultSet rs) throws SQLException {
        String uuid = rs.getString(FIELD_UUID);
        String filename = rs.getString(FIELD_FILENAME);
        String dob = rs.getString(FIELD_DOB);
        Gender gender = Gender.valueOf(rs.getString(FIELD_GENDER));
        Ethnicity ethnicity = Ethnicity.valueOf(rs.getString(FIELD_ETHNICITY));
        YesNoAbstain eal = YesNoAbstain.valueOf(rs.getString(FIELD_EAL));
        YesNoAbstain disabilityStatus = YesNoAbstain.valueOf(rs.getString(FIELD_DISABILITY_STATUS));
        DisabilityType disabilityType = DisabilityType.valueOf(rs.getString(FIELD_DISABILITY_TYPE));
        CareExperience careExperience = CareExperience.valueOf(rs.getString(FIELD_CARE_EXPERIENCE));
        InterventionType interventionType = InterventionType.valueOf(rs.getString(FIELD_INTERVENTION_TYPE));
        Aces aces = Aces.valueOf(rs.getString(FIELD_ACES));
        FundingSource fundingSource = FundingSource.valueOf(rs.getString(FIELD_FUNDING_SOURCE));
        return new UploadFile(UUID.fromString(uuid), filename,
                dob,
                gender,
                ethnicity,
                eal,
                disabilityStatus,
                disabilityType,
                careExperience,
                interventionType,
                aces,
                fundingSource);
    }

    public int deleteAll() throws SdqException {
        return handle(dataSource,
                "deleteAll",
                UploadFileTable.deleteAllSQL(),
                PreparedStatement::executeUpdate);
    }
}
