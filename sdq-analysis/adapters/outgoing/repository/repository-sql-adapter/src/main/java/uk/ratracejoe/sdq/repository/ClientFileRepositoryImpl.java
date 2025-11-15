package uk.ratracejoe.sdq.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.ClientFile;
import uk.ratracejoe.sdq.model.DemographicCount;
import uk.ratracejoe.sdq.model.DemographicField;
import uk.ratracejoe.sdq.model.DemographicReport;
import uk.ratracejoe.sdq.tables.ClientFileTable;

@RequiredArgsConstructor
public class ClientFileRepositoryImpl implements ClientFileRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(ClientFileRepositoryImpl.class);
  private final DataSource dataSource;

  public void saveFile(ClientFile file) throws SdqException {
    RepositoryUtils.handle(
        dataSource,
        "saveFile",
        ClientFileTable.insertSQL(),
        stmt -> {
          int paramIndex = 1;
          LocalDate localDate = LocalDate.ofInstant(file.dateOfBirth(), ZoneId.systemDefault());
          Date dateOfBirth = Date.valueOf(localDate);
          stmt.setString(paramIndex++, file.fileId().toString());
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

  @Override
  public DemographicReport getDemographicReport(DemographicField demographic) {
    return RepositoryUtils.handle(
        dataSource,
        "getDemographicReport",
        ClientFileTable.getDemographicReportSQL(demographic),
        stmt -> {
          ResultSet rs = stmt.executeQuery();
          List<DemographicCount> counts = new ArrayList<>();
          while (rs.next()) {
            String text = rs.getString(1);
            Integer count = rs.getInt(2);
            Double percentage = rs.getDouble(3);
            counts.add(new DemographicCount(text, count, percentage));
          }

          return new DemographicReport(counts);
        });
  }

  @Override
  public Optional<ClientFile> getByUUID(UUID fileId) throws SdqException {
    return RepositoryUtils.handle(
        dataSource,
        "getByUUID",
        ClientFileTable.selectByUUID(),
        stmt -> {
          stmt.setString(1, fileId.toString());
          ResultSet rs = stmt.executeQuery();
          if (!rs.next()) return Optional.empty();
          return Optional.of(getFromResultSet(rs));
        });
  }

  public List<ClientFile> getAll() throws SdqException {
    return RepositoryUtils.handle(
        dataSource,
        "getAll",
        ClientFileTable.selectAllSQL(),
        stmt -> {
          List<ClientFile> files = new ArrayList<>();
          ResultSet rs = stmt.executeQuery();
          while (rs.next()) {
            files.add(getFromResultSet(rs));
          }
          return files;
        });
  }

  private record FilterAndValue(DemographicField field, String value) {}

  @Override
  public List<ClientFile> getFiltered(Map<DemographicField, String> filterMap) throws SdqException {
    List<FilterAndValue> filters =
        filterMap.entrySet().stream()
            .map(e -> new FilterAndValue(e.getKey(), e.getValue()))
            .toList();
    String sql =
        ClientFileTable.selectFilteredSql(filters.stream().map(FilterAndValue::field).toList());
    return RepositoryUtils.handle(
        dataSource,
        "getFiltered",
        sql,
        stmt -> {
          AtomicInteger index = new AtomicInteger(1);
          filters.forEach(
              e -> {
                try {
                  stmt.setString(index.getAndIncrement(), e.value);
                } catch (SQLException ex) {
                  throw new RuntimeException(ex);
                }
              });
          List<ClientFile> files = new ArrayList<>();
          ResultSet rs = stmt.executeQuery();
          while (rs.next()) {
            files.add(getFromResultSet(rs));
          }
          return files;
        });
  }

  private ClientFile getFromResultSet(ResultSet rs) throws SQLException {
    String uuid = rs.getString(ClientFileTable.FIELD_FILE_ID);
    String filename = rs.getString(ClientFileTable.FIELD_FILENAME);
    Date dob = rs.getDate(ClientFileTable.FIELD_DOB);
    String gender = rs.getString(ClientFileTable.FIELD_GENDER);
    String council = rs.getString(ClientFileTable.FIELD_COUNCIL);
    String ethnicity = rs.getString(ClientFileTable.FIELD_ETHNICITY);
    String eal = rs.getString(ClientFileTable.FIELD_EAL);
    String disabilityStatus = rs.getString(ClientFileTable.FIELD_DISABILITY_STATUS);
    String disabilityType = rs.getString(ClientFileTable.FIELD_DISABILITY_TYPE);
    String careExperience = rs.getString(ClientFileTable.FIELD_CARE_EXPERIENCE);
    Integer aces = rs.getInt(ClientFileTable.FIELD_ACES);
    String fundingSource = String.valueOf(rs.getString(ClientFileTable.FIELD_FUNDING_SOURCE));
    return new ClientFile(
        UUID.fromString(uuid),
        filename,
        RepositoryUtils.toInstant(dob),
        gender,
        council,
        ethnicity,
        eal,
        disabilityStatus,
        disabilityType,
        careExperience,
        Collections.emptyList(),
        aces,
        fundingSource);
  }

  public int deleteAll() throws SdqException {
    return RepositoryUtils.handle(
        dataSource, "deleteAll", ClientFileTable.deleteAllSQL(), PreparedStatement::executeUpdate);
  }
}
