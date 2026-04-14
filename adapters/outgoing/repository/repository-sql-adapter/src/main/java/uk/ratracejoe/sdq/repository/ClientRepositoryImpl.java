package uk.ratracejoe.sdq.repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
            Optional.ofNullable(client).map(SdqClient::gender).map(Gender::name).orElse(null))
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client).map(SdqClient::council).map(Council::name).orElse(null))
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client).map(SdqClient::ethnicity).map(Ethnicity::name).orElse(null))
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client)
                .map(SdqClient::englishAdditionalLanguage)
                .map(EnglishAsAdditionalLanguage::name)
                .orElse(null))
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client)
                .map(SdqClient::disabilityStatus)
                .map(DisabilityStatus::name)
                .orElse(null))
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client)
                .map(SdqClient::disabilityType)
                .map(DisabilityType::name)
                .orElse(null))
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client)
                .map(SdqClient::careExperience)
                .map(CareExperience::name)
                .orElse(null))
        .param(paramIndex.getAndIncrement(), client.aces())
        .param(
            paramIndex.getAndIncrement(),
            Optional.ofNullable(client)
                .map(SdqClient::fundingSource)
                .map(FundingSource::name)
                .orElse(null))
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
    return jdbcClient
        .sql("SELECT * FROM client WHERE client_id=?")
        .param(1, clientId) // positional parameter
        .query((rs, rowNum) -> getFromResultSet(rs))
        .single();
  }

  public List<SdqClient> getAll() throws SdqException {
    return jdbcClient
        .sql("SELECT * FROM client")
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
    UUID uuid = rs.getObject("client_id", UUID.class);
    String codeName = rs.getString("code_name");
    Date dob = rs.getDate("date_of_birth");
    Gender gender =
        Optional.ofNullable(rs.getString("gender"))
            .map(Gender::valueOf)
            .orElseGet(Gender::defaultValue);
    Council council =
        Optional.ofNullable(rs.getString("council"))
            .map(Council::valueOf)
            .orElseGet(Council::defaultValue);
    Ethnicity ethnicity =
        Optional.ofNullable(rs.getString("ethnicity"))
            .map(Ethnicity::valueOf)
            .orElseGet(Ethnicity::defaultValue);
    EnglishAsAdditionalLanguage eal =
        Optional.ofNullable(rs.getString("eal"))
            .map(EnglishAsAdditionalLanguage::valueOf)
            .orElseGet(EnglishAsAdditionalLanguage::defaultValue);
    DisabilityStatus disabilityStatus =
        Optional.ofNullable(rs.getString("disability_status"))
            .map(DisabilityStatus::valueOf)
            .orElseGet(DisabilityStatus::defaultValue);
    DisabilityType disabilityType =
        Optional.ofNullable(rs.getString("disability_type"))
            .map(DisabilityType::valueOf)
            .orElseGet(DisabilityType::defaultValue);
    CareExperience careExperience =
        Optional.ofNullable(rs.getString("care_experience"))
            .map(CareExperience::valueOf)
            .orElseGet(CareExperience::defaultValue);
    Integer aces = rs.getInt("aces");
    FundingSource fundingSource =
        Optional.ofNullable(rs.getString("funding_source"))
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
    return jdbcClient.sql("DELETE FROM client").update();
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

  private static final List<String> UPDATE_FIELDS =
      List.of(
          "code_name",
          "date_of_birth",
          "gender",
          "council",
          "ethnicity",
          "eal",
          "disability_status",
          "disability_type",
          "care_experience",
          "aces",
          "funding_source");

  private static final List<String> QUERY_FIELDS =
      Stream.concat(Stream.of("client_id"), UPDATE_FIELDS.stream()).toList();

  static String updateSQL() {
    StringBuilder sb = new StringBuilder();
    sb.append("UPDATE client SET ");
    String fieldNames =
        String.join(",", UPDATE_FIELDS.stream().map(f -> String.format("%s = ?", f)).toList());
    sb.append(fieldNames);
    sb.append(" WHERE ");
    sb.append("client_id");
    sb.append("  = ?");
    return sb.toString();
  }

  static String insertSQL() {
    StringBuilder sb = new StringBuilder();
    sb.append("INSERT INTO client (");
    String fieldNames = String.join(",", QUERY_FIELDS);
    sb.append(fieldNames);
    sb.append(") VALUES (");
    String placeholders = QUERY_FIELDS.stream().map(f -> "?").collect(Collectors.joining(", "));
    sb.append(placeholders);
    sb.append(")");
    return sb.toString();
  }

  static String demographicColumn(DemographicField field) {
    return switch (field) {
      case Gender -> "gender";
      case Council -> "council";
      case Ethnicity -> "ethnicity";
      case EAL -> "eal";
      case DisabilityStatus -> "disability_status";
      case DisabilityType -> "disability_type";
      case CareExperience -> "care_experience";
      case ACES -> "aces";
      case FundingSource -> "funding_source";
      default -> "foo";
    };
  }

  static String getDemographicReportSQL(DemographicField demographic) {
    String columnName = demographicColumn(demographic);
    return String.format(
        "select %s, count(*) AS count, round(100 * count(*) / (select count(*) from client), 2) as percentage FROM client GROUP BY %s;",
        columnName, columnName);
  }

  static String selectFilteredSql(List<DemographicField> fields) {
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT * FROM client ");
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
