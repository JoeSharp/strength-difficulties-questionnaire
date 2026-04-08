package uk.ratracejoe.sdq.repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.*;
import uk.ratracejoe.sdq.model.demographics.*;

@RequiredArgsConstructor
public class ClientRepositoryImpl implements ClientRepository {
  private final JdbcClient jdbcClient;

  public SdqClient createClient(SdqClient client) throws SdqException {
    UUID clientId = Optional.ofNullable(client.clientId()).orElseGet(UUID::randomUUID);
    AtomicInteger paramIndex = new AtomicInteger(1);
    jdbcClient
        .sql(insertSQL())
        .param(paramIndex.getAndIncrement(), clientId)
        .param(paramIndex.getAndIncrement(), client.codeName())
        .param(paramIndex.getAndIncrement(), Date.valueOf(client.dateOfBirth()))
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
    return get(clientId);
  }

  @Override
  public DemographicReport getDemographicReport(DemographicField demographic) {
    String sql = getDemographicReportSQL(demographic);

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
  public SdqClient get(UUID clientId) {
    String sql = String.format("SELECT * FROM %s WHERE %s=?", TABLE_NAME, FIELD_CLIENT_ID);

    return jdbcClient
        .sql(sql)
        .param(1, clientId) // positional parameter
        .query((rs, rowNum) -> getFromResultSet(rs))
        .single();
  }

  public List<SdqClient> getAll() throws SdqException {
    return jdbcClient
        .sql(String.format("SELECT * FROM %s", TABLE_NAME))
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
    String sql = selectFilteredSql(filters.stream().map(FilterAndValue::field).toList());

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
    UUID uuid = rs.getObject(FIELD_CLIENT_ID, UUID.class);
    String codeName = rs.getString(FIELD_CODE_NAME);
    Date dob = rs.getDate(FIELD_DOB);
    Gender gender =
        Optional.ofNullable(rs.getString(FIELD_GENDER))
            .map(Gender::valueOf)
            .orElseGet(Gender::defaultValue);
    Council council =
        Optional.ofNullable(rs.getString(FIELD_COUNCIL))
            .map(Council::valueOf)
            .orElseGet(Council::defaultValue);
    Ethnicity ethnicity =
        Optional.ofNullable(rs.getString(FIELD_ETHNICITY))
            .map(Ethnicity::valueOf)
            .orElseGet(Ethnicity::defaultValue);
    EnglishAsAdditionalLanguage eal =
        Optional.ofNullable(rs.getString(FIELD_EAL))
            .map(EnglishAsAdditionalLanguage::valueOf)
            .orElseGet(EnglishAsAdditionalLanguage::defaultValue);
    DisabilityStatus disabilityStatus =
        Optional.ofNullable(rs.getString(FIELD_DISABILITY_STATUS))
            .map(DisabilityStatus::valueOf)
            .orElseGet(DisabilityStatus::defaultValue);
    DisabilityType disabilityType =
        Optional.ofNullable(rs.getString(FIELD_DISABILITY_TYPE))
            .map(DisabilityType::valueOf)
            .orElseGet(DisabilityType::defaultValue);
    CareExperience careExperience =
        Optional.ofNullable(rs.getString(FIELD_CARE_EXPERIENCE))
            .map(CareExperience::valueOf)
            .orElseGet(CareExperience::defaultValue);
    Integer aces = rs.getInt(FIELD_ACES);
    FundingSource fundingSource =
        Optional.ofNullable(rs.getString(FIELD_FUNDING_SOURCE))
            .map(FundingSource::valueOf)
            .orElseGet(FundingSource::defaultValue);
    return new SdqClient(
        uuid,
        codeName,
        dob.toLocalDate(),
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
    return jdbcClient.sql(String.format("DELETE FROM %s", TABLE_NAME)).update();
  }

  @Override
  public int update(SdqClient client) {
    SdqClient existing = get(client.clientId());

    AtomicInteger paramIndex = new AtomicInteger(1);
    return jdbcClient
        .sql(updateSQL())
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client.codeName()).orElseGet(existing::codeName))
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client.dateOfBirth()).orElseGet(existing::dateOfBirth))
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client.gender()).orElseGet(existing::gender).name())
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client.council()).orElseGet(existing::council).name())
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client.ethnicity()).orElseGet(existing::ethnicity).name())
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client.englishAdditionalLanguage())
                .orElseGet(existing::englishAdditionalLanguage)
                .name())
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client.disabilityStatus())
                .orElseGet(existing::disabilityStatus)
                .name())
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client.disabilityType()).orElseGet(existing::disabilityType).name())
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client.careExperience()).orElseGet(existing::careExperience).name())
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client.aces()).orElseGet(existing::aces))
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client.fundingSource()).orElseGet(existing::fundingSource).name())
        .param(paramIndex.getAndIncrement(), client.clientId())
        .update();
  }

  private static final String TABLE_NAME = "client";
  private static final String FIELD_CLIENT_ID = "client_id";
  private static final String FIELD_CODE_NAME = "code_name";
  private static final String FIELD_DOB = "date_of_birth";
  private static final String FIELD_GENDER = "gender";
  private static final String FIELD_COUNCIL = "council";
  private static final String FIELD_ETHNICITY = "ethnicity";
  private static final String FIELD_EAL = "eal";
  private static final String FIELD_DISABILITY_STATUS = "disability_status";
  private static final String FIELD_DISABILITY_TYPE = "disability_type";
  private static final String FIELD_CARE_EXPERIENCE = "care_experience";
  private static final String FIELD_ACES = "aces";
  private static final String FIELD_FUNDING_SOURCE = "funding_source";
  private static final List<String> FIELDS =
      List.of(
          FIELD_CLIENT_ID,
          FIELD_CODE_NAME,
          FIELD_DOB,
          FIELD_GENDER,
          FIELD_COUNCIL,
          FIELD_ETHNICITY,
          FIELD_EAL,
          FIELD_DISABILITY_STATUS,
          FIELD_DISABILITY_TYPE,
          FIELD_CARE_EXPERIENCE,
          FIELD_ACES,
          FIELD_FUNDING_SOURCE);

  static String updateSQL() {
    StringBuilder sb = new StringBuilder();
    sb.append("UPDATE " + TABLE_NAME + " SET ");
    String fieldNames =
        String.join(
            ",",
            FIELDS.stream()
                .filter(f -> !FIELD_CLIENT_ID.equals(f))
                .map(f -> String.format("%s = ?", f))
                .toList());
    sb.append(fieldNames);
    sb.append(" WHERE ");
    sb.append(FIELD_CLIENT_ID);
    sb.append("  = ?");
    return sb.toString();
  }

  static String insertSQL() {
    StringBuilder sb = new StringBuilder();
    sb.append("INSERT INTO " + TABLE_NAME + " (");
    String fieldNames = String.join(",", FIELDS);
    sb.append(fieldNames);
    sb.append(") VALUES (");
    String placeholders = FIELDS.stream().map(f -> "?").collect(Collectors.joining(", "));
    sb.append(placeholders);
    sb.append(")");
    return sb.toString();
  }

  static String demographicColumn(DemographicField field) {
    return switch (field) {
      case Gender -> ClientRepositoryImpl.FIELD_GENDER;
      case Council -> ClientRepositoryImpl.FIELD_COUNCIL;
      case Ethnicity -> ClientRepositoryImpl.FIELD_ETHNICITY;
      case EAL -> ClientRepositoryImpl.FIELD_EAL;
      case DisabilityStatus -> ClientRepositoryImpl.FIELD_DISABILITY_STATUS;
      case DisabilityType -> ClientRepositoryImpl.FIELD_DISABILITY_TYPE;
      case CareExperience -> ClientRepositoryImpl.FIELD_CARE_EXPERIENCE;
      case ACES -> ClientRepositoryImpl.FIELD_ACES;
      case FundingSource -> ClientRepositoryImpl.FIELD_FUNDING_SOURCE;
      default -> "foo";
    };
  }

  static String getDemographicReportSQL(DemographicField demographic) {
    String columnName = demographicColumn(demographic);
    return String.format(
        "select %s, count(*) AS count, round(100 * count(*) / (select count(*) from %s), 2) as percentage FROM %s GROUP BY %s;",
        columnName, ClientRepositoryImpl.TABLE_NAME, ClientRepositoryImpl.TABLE_NAME, columnName);
  }

  static String selectFilteredSql(List<DemographicField> fields) {
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT * FROM ");
    sql.append(TABLE_NAME);
    if (!fields.isEmpty()) {
      sql.append(
          String.format(
              " WHERE %s",
              fields.stream()
                  .map(ClientRepositoryImpl::demographicColumn)
                  .map(column -> String.format("%s = ?", column))
                  .collect(Collectors.joining(" AND "))));
    }
    return sql.toString();
  }
}
