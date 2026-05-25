package uk.ratracejoe.sdq.repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.SdqClient;
import uk.ratracejoe.sdq.model.demographics.*;

@RequiredArgsConstructor
public class ClientRepositoryImpl implements ClientRepository {
  private final JdbcClient jdbcClient;

  public SdqClient createClient(SdqClient client) throws SdqException {
    UUID clientId = Optional.ofNullable(client.clientId()).orElseGet(UUID::randomUUID);

    List<String> fields = new ArrayList<>();
    Map<String, Object> params = new HashMap<>();
    Function<String, Consumer<Object>> addField =
        (fieldName) ->
            v -> {
              params.put(fieldName, v);
              fields.add(fieldName);
            };
    addField.apply("client_id").accept(clientId);
    addField.apply("code_name").accept(client.codeName());
    addField.apply("date_of_birth").accept(Date.valueOf(client.dateOfBirth()));

    Optional.ofNullable(client.gender()).map(Gender::name).ifPresent(addField.apply("gender"));
    Optional.ofNullable(client.council()).map(Council::name).ifPresent(addField.apply("council"));
    Optional.ofNullable(client.ethnicity())
        .map(Ethnicity::name)
        .ifPresent(addField.apply("ethnicity"));
    Optional.ofNullable(client.englishAdditionalLanguage())
        .map(EnglishAsAdditionalLanguage::name)
        .ifPresent(addField.apply("eal"));
    Optional.ofNullable(client.disabilityStatus())
        .map(DisabilityStatus::name)
        .ifPresent(addField.apply("disability_status"));
    Optional.ofNullable(client.careExperience())
        .map(CareExperience::name)
        .ifPresent(addField.apply("care_experience"));
    Optional.ofNullable(client.fundingSource())
        .map(FundingSource::name)
        .ifPresent(addField.apply("funding_source"));

    StringBuilder sql = new StringBuilder();
    sql.append("INSERT INTO client (");
    String fieldNames = String.join(",", fields);
    sql.append(fieldNames);
    sql.append(") VALUES (");
    String placeholders = fields.stream().map(f -> ":" + f).collect(Collectors.joining(", "));
    sql.append(placeholders);
    sql.append(")");
    jdbcClient.sql(sql.toString()).params(params).update();
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

  @Override
  public List<SdqClient> getFiltered(String partialName, List<DemographicFilter> filters) {
    // Build SQL dynamically based on fields
    String sql = selectFilteredSql(partialName, filters.stream().toList());

    // Bind positional parameters
    Map<String, Object> params = new HashMap<>();
    addFilters(params, filters);
    if (partialName != null) {
      params.put("partial_name", partialName);
    }

    // Execute + map
    return jdbcClient.sql(sql).params(params).query((rs, rowNum) -> getFromResultSet(rs)).list();
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
    CareExperience careExperience =
        Optional.ofNullable(rs.getString("care_experience"))
            .map(CareExperience::valueOf)
            .orElseGet(CareExperience::defaultValue);
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
        Collections.emptyList(),
        careExperience,
        Collections.emptyList(),
        Collections.emptyMap(),
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

  @Override
  public int deleteByClientId(UUID clientId) {
    return jdbcClient
        .sql("DELETE FROM client c WHERE c.client_id = :client_id")
        .param("client_id", clientId)
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

  static String selectFilteredSql(String partialName, List<DemographicFilter> filters) {
    boolean filterOnName = partialName != null && !partialName.isEmpty();
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT * FROM client");
    if (!filters.isEmpty() || filterOnName) {
      sql.append(" WHERE ");
      if (!filters.isEmpty()) {
        sql.append(filterSelectWhere("client", filters));
      }
      if (filterOnName) {
        if (!filters.isEmpty()) {
          sql.append(" AND ");
        }

        sql.append("code_name LIKE CONCAT('%', :partial_name, '%')");
      }
    }
    return sql.toString();
  }

  public static void addFilters(Map<String, Object> params, List<DemographicFilter> filters) {
    for (DemographicFilter fv : filters) {
      params.put(demographicColumn(fv.field()), fv.values());
    }
  }

  public static String filterSelectWhere(String tableAlias, List<DemographicFilter> fields) {
    return fields.stream()
        .map(DemographicFilter::field)
        .map(ClientRepositoryImpl::demographicColumn)
        .map(column -> String.format("%s.%s IN (:%s)", tableAlias, column, column))
        .collect(Collectors.joining(" AND "));
  }
}
