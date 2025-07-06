package uk.ratracejoe.sdq_analysis.database.repository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq_analysis.database.entity.ClientFileEntity;
import uk.ratracejoe.sdq_analysis.database.tables.ClientFileTable;
import uk.ratracejoe.sdq_analysis.exception.SdqException;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static uk.ratracejoe.sdq_analysis.database.repository.RepositoryUtils.handle;
import static uk.ratracejoe.sdq_analysis.database.repository.RepositoryUtils.toInstant;
import static uk.ratracejoe.sdq_analysis.database.tables.ClientFileTable.*;

@Service
@RequiredArgsConstructor
public class ClientFileRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientFileRepository.class);
    private final DataSource dataSource;

    public void saveFile(ClientFileEntity file) throws SdqException {
        handle(dataSource, "saveFile", ClientFileTable.insertSQL(), stmt -> {
            int paramIndex = 1;
            LocalDate localDate = LocalDate.ofInstant(file.dateOfBirth(), ZoneId.systemDefault());
            Date dateOfBirth = Date.valueOf(localDate);
            stmt.setString(paramIndex++, file.uuid().toString());
            stmt.setString(paramIndex++, file.filename());
            stmt.setDate(paramIndex++, dateOfBirth);
            stmt.setString(paramIndex++, file.gender());
            stmt.setString(paramIndex++, file.council());
            stmt.setString(paramIndex++, file.ethnicity());
            stmt.setString(paramIndex++, file.englishAdditionalLanguage());
            stmt.setString(paramIndex++, file.disabilityStatus());
            stmt.setString(paramIndex++, file.disabilityType());
            stmt.setString(paramIndex++, file.careExperience());
            stmt.setInt(paramIndex++, file.aces());
            stmt.setString(paramIndex++, file.fundingSource());
            int rowsUpdated = stmt.executeUpdate();
            LOGGER.info("Inserted File to database, rows updated {}", rowsUpdated);
            return rowsUpdated;
        });
    }

    public Optional<ClientFileEntity> getByUUID(UUID uuid) throws SdqException {
        return handle(dataSource, "getByUUID", ClientFileTable.selectByUUID(), stmt -> {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) return Optional.empty();
            return Optional.of(getFromResultSet(rs));
        });
    }

    public List<ClientFileEntity> getAll() throws SdqException {
        return handle(dataSource, "getAll", ClientFileTable.selectAllSQL(), stmt -> {
            List<ClientFileEntity> files = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                files.add(getFromResultSet(rs));
            }
            return files;
        });
    }

    private ClientFileEntity getFromResultSet(ResultSet rs) throws SQLException {
        String uuid = rs.getString(FIELD_UUID);
        String filename = rs.getString(FIELD_FILENAME);
        Date dob = rs.getDate(FIELD_DOB);
        String gender = rs.getString(FIELD_GENDER);
        String council = rs.getString(FIELD_COUNCIL);
        String ethnicity = rs.getString(FIELD_ETHNICITY);
        String eal = rs.getString(FIELD_EAL);
        String disabilityStatus = rs.getString(FIELD_DISABILITY_STATUS);
        String disabilityType = rs.getString(FIELD_DISABILITY_TYPE);
        String careExperience = rs.getString(FIELD_CARE_EXPERIENCE);
        Integer aces = rs.getInt(FIELD_ACES);
        String fundingSource = String.valueOf(rs.getString(FIELD_FUNDING_SOURCE));
        return new ClientFileEntity(UUID.fromString(uuid), filename,
                toInstant(dob),
                gender,
                council,
                ethnicity,
                eal,
                disabilityStatus,
                disabilityType,
                careExperience,
                aces,
                fundingSource);
    }

    public int deleteAll() throws SdqException {
        return handle(dataSource,
                "deleteAll",
                ClientFileTable.deleteAllSQL(),
                PreparedStatement::executeUpdate);
    }
}
