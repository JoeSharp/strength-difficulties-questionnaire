package uk.ratracejoe.sdq.repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.DemographicCount;
import uk.ratracejoe.sdq.model.DemographicField;
import uk.ratracejoe.sdq.model.DemographicReport;
import uk.ratracejoe.sdq.model.SdqClient;
import uk.ratracejoe.sdq.tables.ClientFileTable;

@RequiredArgsConstructor
public class SdqClientRepositoryImpl implements SdqClientRepository {
  private final JdbcClient jdbcClient;

  public SdqClient createClient(SdqClient client) throws SdqException {
    LocalDate localDate = LocalDate.ofInstant(client.dateOfBirth(), ZoneId.systemDefault());
    Date dateOfBirth = Date.valueOf(localDate);
    UUID clientId = Optional.ofNullable(client.clientId()).orElseGet(UUID::randomUUID);
    AtomicInteger paramIndex = new AtomicInteger(1);
    jdbcClient
        .sql(ClientFileTable.insertSQL())
        .param(paramIndex.getAndIncrement(), clientId)
        .param(paramIndex.getAndIncrement(), client.codeName())
        .param(paramIndex.getAndIncrement(), dateOfBirth)
        .param(paramIndex.getAndIncrement(), client.gender())
        .param(paramIndex.getAndIncrement(), client.council())
        .param(paramIndex.getAndIncrement(), client.ethnicity())
        .param(paramIndex.getAndIncrement(), client.englishAdditionalLanguage())
        .param(paramIndex.getAndIncrement(), client.disabilityStatus())
        .param(paramIndex.getAndIncrement(), client.disabilityType())
        .param(paramIndex.getAndIncrement(), client.careExperience())
        .param(paramIndex.getAndIncrement(), client.aces())
        .param(paramIndex.getAndIncrement(), client.fundingSource())
        .update();
    return getByUUID(clientId).orElseThrow(() -> new SdqException("Failed to create client"));
  }

  @Override
  public DemographicReport getDemographicReport(DemographicField demographic) {
    String sql = ClientFileTable.getDemographicReportSQL(demographic);

    List<DemographicCount> counts =
        jdbcClient
            .sql(sql)
            .query(
                (rs, rowNum) ->
                    new DemographicCount(rs.getString(1), rs.getInt(2), rs.getDouble(3)))
            .list();

    return new DemographicReport(counts);
  }

  @Override
  public Optional<SdqClient> getByUUID(UUID fileId) {
    String sql = ClientFileTable.selectByUUID();

    return jdbcClient
        .sql(sql)
        .param(1, fileId) // positional parameter
        .query((rs, rowNum) -> getFromResultSet(rs))
        .optional();
  }

  public List<SdqClient> getAll() throws SdqException {
    return jdbcClient
        .sql(ClientFileTable.selectAllSQL())
        .query((rs, rowNum) -> getFromResultSet(rs))
        .list();
  }

  private record FilterAndValue(DemographicField field, String value) {}

  @Override
  public List<SdqClient> getFiltered(Map<DemographicField, String> filterMap) {
    // Build the list of filters
    List<FilterAndValue> filters =
        filterMap.entrySet().stream()
            .map(e -> new FilterAndValue(e.getKey(), e.getValue()))
            .toList();

    // Build SQL dynamically based on fields
    String sql =
        ClientFileTable.selectFilteredSql(filters.stream().map(FilterAndValue::field).toList());

    JdbcClient.StatementSpec stmt = jdbcClient.sql(sql);

    // Bind positional parameters
    int index = 1;
    for (FilterAndValue fv : filters) {
      stmt = stmt.param(index++, fv.value());
    }

    // Execute + map
    return stmt.query((rs, rowNum) -> getFromResultSet(rs)).list();
  }

  private SdqClient getFromResultSet(ResultSet rs) throws SQLException {
    UUID uuid = rs.getObject(ClientFileTable.FIELD_CLIENT_ID, UUID.class);
    String codeName = rs.getString(ClientFileTable.FIELD_CODE_NAME);
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
    return new SdqClient(
        uuid,
        codeName,
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

  public int deleteAll() {
    return jdbcClient.sql(ClientFileTable.deleteAllSQL()).update();
  }
}
