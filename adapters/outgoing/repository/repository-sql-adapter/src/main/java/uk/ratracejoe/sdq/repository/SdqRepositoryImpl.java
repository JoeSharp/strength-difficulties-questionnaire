package uk.ratracejoe.sdq.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.demographics.DemographicFilter;
import uk.ratracejoe.sdq.model.sdq.*;

import java.time.LocalDate;
import java.util.*;

import static uk.ratracejoe.sdq.repository.RepositoryJsonUtils.parseJson;

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
  public SdqProgressSummary getProgressForClient(UUID clientId, Assessor assessor) {
    String whereClause = "AND cl.client_id = :client_id";
    Map<String, Object> params = new HashMap<>();
    params.put("assessor", assessor.name());
    params.put("client_id", clientId);
    return jdbcClient
        .sql(String.format(GET_PROGRESS_SUMMARY_SQL, whereClause))
        .params(params)
        .query(
            (rs, rowNum) -> {
              String categoryJson = rs.getString("category_progress");
              String postureJson = rs.getString("posture_progress");
              Map<String, Progress> byCategory =
                  parseJson(categoryJson, new TypeReference<>() {}, Collections::emptyMap);
              Map<Posture, Progress> byPosture =
                  convertPosture(
                      parseJson(postureJson, new TypeReference<>() {}, Collections::emptyMap));

              Progress totalDifficulties =
                  parseJson(rs.getString("total_progress"), Progress.class);

              return SdqProgressSummary.builder()
                  .clientId(clientId)
                  .assessor(assessor)
                  .categoryProgress(byCategory)
                  .postureProgress(byPosture)
                  .totalDifficulties(totalDifficulties)
                  .build();
            })
        .single();
  }

  private static final String GET_PROGRESS_SUMMARY_SQL = "SELECT * FROM sdq_summary_full %s";

  @Override
  public List<SdqProgressSummary> getSdqProgress(
      Assessor assessor, List<DemographicFilter> filters, LocalDate from, LocalDate to) {
    StringBuilder whereClause = new StringBuilder();
    whereClause.append("WHERE assessor = :assessor");
    if (!filters.isEmpty()) {
      whereClause.append(" AND last_period_date >= :period_from ");
      whereClause.append(" AND last_period_date < :period_to ");
      whereClause.append(" AND ");
      whereClause.append(ClientRepositoryImpl.filterSelectWhere(filters));
    }
    Map<String, Object> params = new HashMap<>();
    params.put("period_from", from);
    params.put("period_to", to);
    params.put("assessor", assessor.name());
    ClientRepositoryImpl.addFilters(params, filters);
    return jdbcClient
        .sql(String.format(GET_PROGRESS_SUMMARY_SQL, whereClause))
        .params(params)
        .query(
            (rs, rowNum) -> {
              UUID clientId = rs.getObject("client_id", UUID.class);
              String categoryJson = rs.getString("category_progress");
              String postureJson = rs.getString("posture_progress");
              Map<String, Progress> byCategory =
                  parseJson(categoryJson, new TypeReference<>() {}, Collections::emptyMap);
              Map<Posture, Progress> byPosture =
                  convertPosture(
                      parseJson(postureJson, new TypeReference<>() {}, Collections::emptyMap));

              Progress totalDifficulties =
                  parseJson(rs.getString("total_progress"), Progress.class);

              return SdqProgressSummary.builder()
                  .clientId(clientId)
                  .assessor(assessor)
                  .categoryProgress(byCategory)
                  .postureProgress(byPosture)
                  .totalDifficulties(totalDifficulties)
                  .build();
            })
        .list();
  }

  private static final String GET_FILTERED_SUMMARY_SQL =
      """
      WITH base AS (
        SELECT
          cl.client_id as client_id,
          p.period_id as period_id,
          p.period_date as period_date,
          s.statement AS statement_key,
          st.category AS category,
          c.posture AS posture,
          s.score AS score
        FROM
          sdq s
          INNER JOIN reporting_period p ON p.period_id = s.period_id
          INNER JOIN client cl ON cl.client_id = p.client_id
          INNER JOIN sdq_statement st ON st.statement_key = s.statement
          INNER JOIN sdq_category c ON c.category = st.category
          WHERE assessor = :assessor
          AND p.period_date >= :period_from
          AND p.period_date <  :period_to
          %s
      ),
      category_totals AS (
          SELECT client_id, period_id, category, SUM(score) AS total
          FROM base
          GROUP BY client_id, period_id, category
      ),
      posture_totals AS (
          SELECT client_id, period_id, posture, SUM(score) AS total
          FROM base
          GROUP BY client_id, period_id, posture
      ),
      total_difficulties AS (
          SELECT client_id, period_id, SUM(score) AS total
          FROM base
          WHERE posture <> 'ProSocial'
          GROUP BY client_id, period_id
      ),
      period_summary AS (
        SELECT
          b.client_id,
          b.period_id,
          b.period_date,
          (
              SELECT JSONB_OBJECT_AGG(category, total)
              FROM category_totals ct
              WHERE ct.client_id = b.client_id
                AND ct.period_id = b.period_id
          ) AS category_subtotals,
          (
              SELECT JSONB_OBJECT_AGG(posture, total)
              FROM posture_totals pt
              WHERE pt.client_id = b.client_id
                AND pt.period_id = b.period_id
          ) AS posture_subtotals,
          (
              SELECT total
              FROM total_difficulties td
              WHERE td.client_id = b.client_id
                AND td.period_id = b.period_id
          ) AS total_difficulties
          FROM base b
          GROUP BY b.client_id, b.period_id, b.period_date
          ORDER BY b.client_id, b.period_date
      )
      SELECT * from period_summary
                  """;

  @Override
  public List<SdqSubmissionSummary> getFiltered(
      Assessor assessor, List<DemographicFilter> filters, LocalDate from, LocalDate to) {
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
              UUID clientId = rs.getObject("client_id", UUID.class);
              LocalDate periodDate = rs.getDate("period_date").toLocalDate();
              String categoryJson = rs.getString("category_subtotals");
              String postureJson = rs.getString("posture_subtotals");
              Map<String, Integer> byCategory =
                  parseJson(categoryJson, new TypeReference<>() {}, Collections::emptyMap);
              Map<Posture, Integer> byPosture =
                  convertPosture(
                      parseJson(postureJson, new TypeReference<>() {}, Collections::emptyMap));

              int totalDifficulties = rs.getInt("total_difficulties");

              return SdqSubmissionSummary.builder()
                  .clientId(clientId)
                  .period(periodDate)
                  .assessor(assessor)
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
              Map<String, Integer> byCategory =
                  parseJson(categoryJson, new TypeReference<>() {}, Collections::emptyMap);
              Map<Posture, Integer> byPosture =
                  convertPosture(
                      parseJson(postureJson, new TypeReference<>() {}, Collections::emptyMap));

              int totalDifficulties = rs.getInt("total_difficulties");

              return SdqSubmissionSummary.builder()
                  .categorySubTotals(byCategory)
                  .postureSubTotals(byPosture)
                  .totalDifficulties(totalDifficulties)
                  .build();
            })
        .single();
  }

  private static <T> Map<Posture, T> convertPosture(Map<String, T> raw) {
    Map<Posture, T> result = new EnumMap<>(Posture.class);
    raw.forEach((k, v) -> result.put(Posture.valueOf(k), v));
    return result;
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
