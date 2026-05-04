package uk.ratracejoe.sdq.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.demographics.DemographicFilter;
import uk.ratracejoe.sdq.model.sdq.Posture;
import uk.ratracejoe.sdq.model.sdq.SdqScore;
import uk.ratracejoe.sdq.model.sdq.SdqSubmission;
import uk.ratracejoe.sdq.model.sdq.SdqSubmissionSummary;

@RequiredArgsConstructor
public class SdqRepositoryImpl implements SdqRepository {
  private final JdbcClient jdbcClient;

  private final StatementRepository statementRepository;

  @Override
  public void save(SdqSubmission sdq) throws SdqException {
    sdq.scores()
        .forEach(
            score ->
                jdbcClient
                    .sql(
                        """
                INSERT INTO sdq
                  (period_id, assessor, statement, score)
                VALUES
                  (:periodId, :assessor, :statement, :score)""")
                    .param("periodId", sdq.periodId())
                    .param("assessor", sdq.assessor().name())
                    .param("statement", score.statement().key())
                    .param("score", score.score())
                    .update());
  }

  @Override
  public SdqSubmission get(UUID periodId, Assessor assessor) {
    return SdqSubmission.builder()
        .periodId(periodId)
        .assessor(assessor)
        .scores(getScores(periodId, assessor))
        .build();
  }

  public int deleteAll() throws SdqException {
    return jdbcClient.sql("DELETE FROM sdq").update();
  }

  @Override
  public List<SdqSubmissionSummary> getSdqProgress(
      Assessor assessor, LocalDate from, LocalDate to) {
    return Collections.emptyList();
  }

  private static final String GET_FILTERED_SUMMARY_SQL =
      """
              WITH base AS (
                SELECT
                  s.statement AS statement_key,
                  st.category AS category,
                  s.score AS score,
                  c.posture AS posture
                FROM
                  sdq s
                  INNER JOIN sdq_statement st ON st.statement_key = s.statement
                  INNER JOIN sdq_category c ON c.category = st.category
                  INNER JOIN reporting_period p ON p.period_id = s.period_id
                  INNER JOIN client cl ON cl.client_id = p.client_id
                  WHERE assessor = :assessor
                  AND p.period_date >= :period_from
                  AND p.period_date <  :period_to
                  %s
              ),

              category_totals AS (
                SELECT category, SUM(score) AS total
                FROM base
                GROUP BY category
              ),

              posture_totals AS (
                SELECT posture, SUM(score) AS total
                FROM base
                GROUP BY posture
              ),

              total_difficulties AS (
                SELECT SUM(score) AS total
                FROM base
                WHERE posture <> 'ProSocial'
              )

              SELECT
                (SELECT JSONB_OBJECT_AGG(category, total) FROM category_totals) AS category_subtotals,
                (SELECT JSONB_OBJECT_AGG(posture, total) FROM posture_totals) AS posture_subtotals,
                (SELECT total FROM total_difficulties) AS total_difficulties;
                  """;

  @Override
  public List<SdqSubmissionSummary> getFilteredSdqs(
      Assessor assessor,
      String category,
      List<DemographicFilter> filters,
      LocalDate from,
      LocalDate to) {
    StringBuilder whereClause = new StringBuilder();
    if (!filters.isEmpty()) {
      whereClause.append(" AND ");
      whereClause.append(ClientRepositoryImpl.filterSelectWhere("cl", filters));
    }
    Map<String, Object> params = new HashMap<>();
    params.put("period_from", from);
    params.put("period_to", to);
    params.put("assessor", assessor.name());
    ClientRepositoryImpl.addFilters(params, filters);
    return jdbcClient
        .sql(String.format(GET_FILTERED_SUMMARY_SQL, whereClause))
        .params(params)
        .query(
            (rs, rowNum) -> {
              String categoryJson = rs.getString("category_subtotals");
              String postureJson = rs.getString("posture_subtotals");
              Map<String, Integer> byCategory = parseMap(categoryJson);
              Map<Posture, Integer> byPosture = convertPosture(parseMap(postureJson));

              int totalDifficulties = rs.getInt("total_difficulties");

              return SdqSubmissionSummary.builder()
                  .categorySubTotals(byCategory)
                  .postureSubTotals(byPosture)
                  .totalDifficulties(totalDifficulties)
                  .build();
            })
        .list();
  }

  private static final String GET_SUMMARY_SQL =
      """
          WITH base AS (
            SELECT
              s.statement AS statement_key,
              st.category AS category,
              s.score AS score,
              c.posture AS posture
            FROM
              sdq s
              INNER JOIN sdq_statement st ON st.statement_key = s.statement
              INNER JOIN sdq_category c ON c.category = st.category
              WHERE period_id = :periodId AND assessor = :assessor
          ),

          category_totals AS (
            SELECT category, SUM(score) AS total
            FROM base
            GROUP BY category
          ),

          posture_totals AS (
            SELECT posture, SUM(score) AS total
            FROM base
            GROUP BY posture
          ),

          total_difficulties AS (
            SELECT SUM(score) AS total
            FROM base
            WHERE posture <> 'ProSocial'
          )

          SELECT
            (SELECT JSONB_OBJECT_AGG(category, total) FROM category_totals) AS category_subtotals,
            (SELECT JSONB_OBJECT_AGG(posture, total) FROM posture_totals) AS posture_subtotals,
            (SELECT total FROM total_difficulties) AS total_difficulties;
              """;

  @Override
  public SdqSubmissionSummary getSummary(UUID periodId, Assessor assessor) {
    return jdbcClient
        .sql(GET_SUMMARY_SQL)
        .param("periodId", periodId)
        .param("assessor", assessor.name())
        .query(
            (rs, rowNum) -> {
              String categoryJson = rs.getString("category_subtotals");
              String postureJson = rs.getString("posture_subtotals");
              Map<String, Integer> byCategory = parseMap(categoryJson);
              Map<Posture, Integer> byPosture = convertPosture(parseMap(postureJson));

              int totalDifficulties = rs.getInt("total_difficulties");

              return SdqSubmissionSummary.builder()
                  .categorySubTotals(byCategory)
                  .postureSubTotals(byPosture)
                  .totalDifficulties(totalDifficulties)
                  .build();
            })
        .single();
  }

  private static Map<Posture, Integer> convertPosture(Map<String, Integer> raw) {
    Map<Posture, Integer> result = new EnumMap<>(Posture.class);
    raw.forEach((k, v) -> result.put(Posture.valueOf(k), v));
    return result;
  }

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static Map<String, Integer> parseMap(String json) {
    if (json == null) return Map.of();
    try {
      return MAPPER.readValue(json, new TypeReference<>() {});
    } catch (Exception e) {
      throw new SdqException("Failed to parse JSON: " + json, e);
    }
  }

  private List<SdqScore> getScores(UUID periodId, Assessor assessor) {
    return jdbcClient
        .sql(
            """
          SELECT
            s.statement AS statement_key,
            s.score as score
          FROM
            sdq s
          INNER JOIN sdq_statement st
          ON s.statement = st.statement_key
          WHERE period_id = :periodId AND assessor = :assessor
        """)
        .param("periodId", periodId)
        .param("assessor", assessor.name())
        .query(
            (rs, rowNum) ->
                SdqScore.builder()
                    .statement(statementRepository.getStatement(rs.getString("statement_key")))
                    .score(rs.getInt("score"))
                    .build())
        .list();
  }
}
