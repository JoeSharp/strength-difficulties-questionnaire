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
import uk.ratracejoe.sdq.model.*;
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
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client.gender()).map(Gender::name).orElse(null))
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client.council()).map(Council::name).orElse(null))
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client.ethnicity()).map(Ethnicity::name).orElse(null))
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client.englishAdditionalLanguage())
                .map(EnglishAsAdditionalLanguage::name)
                .orElse(null))
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client.disabilityStatus()).map(DisabilityStatus::name).orElse(null))
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client.disabilityType()).map(DisabilityType::name).orElse(null))
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client.careExperience()).map(CareExperience::name).orElse(null))
        .param(paramIndex.getAndIncrement(), client.aces())
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client.fundingSource()).map(FundingSource::name).orElse(null))
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
    Gender gender =
        Optional.ofNullable(rs.getString(ClientFileTable.FIELD_GENDER))
            .map(Gender::valueOf)
            .orElseGet(Gender::defaultValue);
    Council council =
        Optional.ofNullable(rs.getString(ClientFileTable.FIELD_COUNCIL))
            .map(Council::valueOf)
            .orElseGet(Council::defaultValue);
    Ethnicity ethnicity =
        Optional.ofNullable(rs.getString(ClientFileTable.FIELD_ETHNICITY))
            .map(Ethnicity::valueOf)
            .orElseGet(Ethnicity::defaultValue);
    EnglishAsAdditionalLanguage eal =
        Optional.ofNullable(rs.getString(ClientFileTable.FIELD_EAL))
            .map(EnglishAsAdditionalLanguage::valueOf)
            .orElseGet(EnglishAsAdditionalLanguage::defaultValue);
    DisabilityStatus disabilityStatus =
        Optional.ofNullable(rs.getString(ClientFileTable.FIELD_DISABILITY_STATUS))
            .map(DisabilityStatus::valueOf)
            .orElseGet(DisabilityStatus::defaultValue);
    DisabilityType disabilityType =
        Optional.ofNullable(rs.getString(ClientFileTable.FIELD_DISABILITY_TYPE))
            .map(DisabilityType::valueOf)
            .orElseGet(DisabilityType::defaultValue);
    CareExperience careExperience =
        Optional.ofNullable(rs.getString(ClientFileTable.FIELD_CARE_EXPERIENCE))
            .map(CareExperience::valueOf)
            .orElseGet(CareExperience::defaultValue);
    Integer aces = rs.getInt(ClientFileTable.FIELD_ACES);
    FundingSource fundingSource =
        Optional.ofNullable(rs.getString(ClientFileTable.FIELD_FUNDING_SOURCE))
            .map(FundingSource::valueOf)
            .orElseGet(FundingSource::defaultValue);
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
