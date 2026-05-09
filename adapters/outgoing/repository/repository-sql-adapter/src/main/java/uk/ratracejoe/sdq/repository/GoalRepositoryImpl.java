package uk.ratracejoe.sdq.repository;

import java.time.LocalDate;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.demographics.DemographicFilter;
import uk.ratracejoe.sdq.model.gbo.Goal;
import uk.ratracejoe.sdq.model.gbo.GoalProgress;
import uk.ratracejoe.sdq.model.gbo.GoalType;

@RequiredArgsConstructor
public class GoalRepositoryImpl implements GoalRepository {

  private final JdbcClient jdbcClient;

  @Override
  public void save(Goal goal) {
    jdbcClient
        .sql(
            """
        INSERT INTO goal
          (client_id, goal_id, type, description)
        VALUES
          (:client_id, :goal_id, :goal_type, :description)
        """)
        .param("client_id", goal.clientId())
        .param("goal_id", goal.goalId())
        .param(
            "goal_type",
            Optional.ofNullable(goal.type())
                .map(GoalType::name)
                .orElse(GoalType.defaultValue().name()))
        .param("description", goal.description())
        .update();
  }

  @Override
  public Goal get(UUID goalId) {
    return jdbcClient
        .sql("SELECT client_id, type, description FROM goal WHERE goal_id = ?")
        .param(1, goalId)
        .query(
            (rs, rowNum) ->
                Goal.builder()
                    .goalId(goalId)
                    .clientId(rs.getObject("client_id", UUID.class))
                    .type(GoalType.valueOf(rs.getString("type")))
                    .description(rs.getString("description"))
                    .build())
        .single();
  }

  @Override
  public GoalProgress getGoalProgress(UUID goalId, Assessor assessor) {
    return jdbcClient
        .sql(
            """
                    SELECT DISTINCT
                      g.client_id as client_id,
                      o.goal_id as goal_id,
                      g.type as goal_type,
                      g.description as goal_description,
                    FIRST_VALUE(o.score) OVER (
                            PARTITION BY g.client_id, o.goal_id
                            ORDER BY o.period_date
                    ) AS first_score,
                    LAST_VALUE(o.score) OVER (
                            PARTITION BY g.client_id, o.goal_id
                            ORDER BY o.period_date
                            RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
                    ) AS last_score
                    FROM gbo_score o
                    JOIN goal g ON g.goal_id = o.goal_id
                    WHERE g.goal_id = :goal_id
                    AND o.assessor = :assessor
                    """)
        .param("goal_id", goalId)
        .param("assessor", assessor.name())
        .query(
            (rs, rowNum) ->
                GoalProgress.builder()
                    .assessor(assessor)
                    .goal(
                        Goal.builder()
                            .clientId(rs.getObject("client_id", UUID.class))
                            .goalId(rs.getObject("goal_id", UUID.class))
                            .type(
                                Optional.ofNullable(rs.getString("goal_type"))
                                    .map(GoalType::valueOf)
                                    .orElseGet(GoalType::defaultValue))
                            .description(rs.getString("goal_description"))
                            .build())
                    .firstScore(rs.getInt("first_score"))
                    .lastScore(rs.getInt("last_score"))
                    .build())
        .optional()
        .orElseGet(
            () ->
                GoalProgress.builder()
                    .goal(get(goalId))
                    .lastScore(0)
                    .firstScore(0)
                    .assessor(assessor)
                    .build());
  }

  @Override
  public List<Goal> getForClient(UUID clientId) {
    return jdbcClient
        .sql("SELECT goal_id, type, description FROM goal WHERE client_id = ?")
        .param(1, clientId)
        .query(
            (rs, rowNum) ->
                Goal.builder()
                    .clientId(clientId)
                    .goalId(rs.getObject("goal_id", UUID.class))
                    .type(
                        Optional.ofNullable(rs.getString("type"))
                            .map(GoalType::valueOf)
                            .orElseGet(GoalType::defaultValue))
                    .description(rs.getString("description"))
                    .build())
        .list();
  }

  @Override
  public int deleteAll() {
    return jdbcClient.sql("DELETE FROM goal").update();
  }

  @Override
  public List<GoalProgress> getGoalsByAssessor(Assessor assessor, LocalDate from, LocalDate to) {
    return jdbcClient
        .sql(
            """
            SELECT DISTINCT
              g.client_id as client_id,
              o.goal_id as goal_id,
              g.type as goal_type,
              g.description as goal_description,
            FIRST_VALUE(o.score) OVER (
                    PARTITION BY g.client_id, o.goal_id
                    ORDER BY o.period_date
            ) AS first_score,
            LAST_VALUE(o.score) OVER (
                    PARTITION BY g.client_id, o.goal_id
                    ORDER BY o.period_date
                    RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
            ) AS last_score
            FROM gbo_score o
            JOIN goal g ON g.goal_id = o.goal_id
            WHERE o.period_date >= :period_from
            AND o.period_date < :period_to
            AND o.assessor = :assessor
            """)
        .param("period_from", from)
        .param("period_to", to)
        .param("assessor", assessor.name())
        .query(
            (rs, rowNum) ->
                GoalProgress.builder()
                    .assessor(assessor)
                    .goal(
                        Goal.builder()
                            .clientId(rs.getObject("client_id", UUID.class))
                            .goalId(rs.getObject("goal_id", UUID.class))
                            .type(
                                Optional.ofNullable(rs.getString("goal_type"))
                                    .map(GoalType::valueOf)
                                    .orElseGet(GoalType::defaultValue))
                            .description(rs.getString("goal_description"))
                            .build())
                    .firstScore(rs.getInt("first_score"))
                    .lastScore(rs.getInt("last_score"))
                    .build())
        .list();
  }

  @Override
  public List<GoalProgress> getGoalsWithProgress(
      Assessor assessor,
      List<DemographicFilter> filters,
      int minProgress,
      List<GoalType> goalTypes,
      LocalDate from,
      LocalDate to) {
    StringBuilder whereClause = new StringBuilder();
    if (!filters.isEmpty() || !goalTypes.isEmpty()) {
      whereClause.append(" WHERE ");
      if (!filters.isEmpty()) {
        whereClause.append(ClientRepositoryImpl.filterSelectWhere("c", filters));
      }
      if (!goalTypes.isEmpty()) {
        if (!filters.isEmpty()) {
          whereClause.append(" AND ");
        }
        whereClause.append(" s.goal_type IN (:goal_types)");
      }
    }
    String sql =
        String.format(
            """
    WITH scored AS (
            SELECT DISTINCT
            g.client_id as client_id,
            g.type as goal_type,
            o.goal_id as goal_id,
            g.description as goal_description,
            FIRST_VALUE(o.score) OVER (
                    PARTITION BY g.client_id, o.goal_id
                    ORDER BY o.period_date
            ) AS first_score,
            LAST_VALUE(o.score) OVER (
                    PARTITION BY g.client_id, o.goal_id
                    ORDER BY o.period_date
                    RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
            ) AS last_score
            FROM gbo_score o
            JOIN goal g ON g.goal_id = o.goal_id
            WHERE o.period_date >= :period_from
            AND o.period_date <  :period_to
    )
    SELECT *
    FROM scored s
    JOIN client c ON c.client_id = s.client_id
    %s
    AND (s.last_score - s.first_score) >= :minProgress;
            """,
            whereClause);
    Map<String, Object> params = new HashMap<>();
    params.put("period_from", from);
    params.put("period_to", to);
    ClientRepositoryImpl.addFilters(params, filters);
    params.put("minProgress", minProgress);
    params.put("assessor", assessor.name());
    params.put("goal_types", goalTypes.stream().map(GoalType::name).toList());
    return jdbcClient
        .sql(sql)
        .params(params)
        .query(
            (rs, rowNum) ->
                GoalProgress.builder()
                    .goal(
                        Goal.builder()
                            .clientId(rs.getObject("client_id", UUID.class))
                            .goalId(rs.getObject("goal_id", UUID.class))
                            .type(
                                Optional.ofNullable(rs.getString("goal_type"))
                                    .map(GoalType::valueOf)
                                    .orElseGet(GoalType::defaultValue))
                            .description(rs.getString("goal_description"))
                            .build())
                    .assessor(assessor)
                    .firstScore(rs.getInt("first_score"))
                    .lastScore(rs.getInt("last_score"))
                    .build())
        .list();
  }

  @Override
  public int update(Goal goal) {
    return jdbcClient
        .sql(
            "UPDATE goal SET type = :goal_type description = :description WHERE goal_id = :goal_id")
        .param("description", goal.description())
        .param(
            "goal_type",
            Optional.ofNullable(goal.type())
                .map(GoalType::name)
                .orElse(GoalType.defaultValue().name()))
        .param("goal_id", goal.goalId())
        .update();
  }
}
