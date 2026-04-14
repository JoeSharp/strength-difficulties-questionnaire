package uk.ratracejoe.sdq.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.model.demographics.Gender;
import uk.ratracejoe.sdq.model.gbo.Goal;
import uk.ratracejoe.sdq.model.gbo.GoalProgress;

@RequiredArgsConstructor
public class GoalRepositoryImpl implements GoalRepository {

  private final JdbcClient jdbcClient;

  @Override
  public void save(Goal goal) {
    jdbcClient
        .sql("INSERT INTO goal (client_id, goal_id, description) VALUES (?, ?, ?)")
        .param(1, goal.clientId())
        .param(2, goal.goalId())
        .param(3, goal.description())
        .update();
  }

  @Override
  public Goal get(UUID goalId) {
    return jdbcClient
        .sql("SELECT client_id, description FROM goal WHERE goal_id = ?")
        .param(1, goalId)
        .query(
            (rs, rowNum) ->
                Goal.builder()
                    .goalId(goalId)
                    .clientId(rs.getObject("client_id", UUID.class))
                    .description(rs.getString("description"))
                    .build())
        .single();
  }

  @Override
  public List<Goal> getForClient(UUID clientId) {
    return jdbcClient
        .sql("SELECT goal_id, description FROM goal WHERE client_id = ?")
        .param(1, clientId)
        .query(
            (rs, rowNum) ->
                Goal.builder()
                    .clientId(clientId)
                    .goalId(rs.getObject("goal_id", UUID.class))
                    .description(rs.getString("description"))
                    .build())
        .list();
  }

  @Override
  public int deleteAll() {
    return jdbcClient.sql("DELETE FROM goal").update();
  }

  @Override
  public List<GoalProgress> getGoalsWithProgress(int minProgress, LocalDate from, LocalDate to) {
    String sql =
        """
    WITH scored AS (
            SELECT
            g.client_id as client_id,
            o.goal_id as goal_id,
            o.period_date as period_date,
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
            WHERE o.period_date >= ?
            AND o.period_date <  ?
    )
    SELECT *
    FROM scored s
    JOIN client c ON c.client_id = s.client_id
    WHERE c.gender = ?
    AND (s.last_score - s.first_score) >= ?;
            """;
    AtomicInteger paramIndex = new AtomicInteger(1);
    return jdbcClient
        .sql(sql)
        .param(paramIndex.getAndIncrement(), from)
        .param(paramIndex.getAndIncrement(), to)
        .param(paramIndex.getAndIncrement(), Gender.MALE.name())
        .param(paramIndex.getAndIncrement(), minProgress)
        .query(
            (rs, rowNum) ->
                GoalProgress.builder()
                    .goal(
                        Goal.builder()
                            .clientId(rs.getObject("client_id", UUID.class))
                            .goalId(rs.getObject("goal_id", UUID.class))
                            .description(rs.getString("goal_description"))
                            .build())
                    .firstScore(rs.getInt("first_score"))
                    .lastScore(rs.getInt("last_score"))
                    .build())
        .list();
  }

  @Override
  public int update(Goal goal) {
    return jdbcClient
        .sql("UPDATE goal SET description = ? WHERE goal_id = ?")
        .param(1, goal.description())
        .param(2, goal.goalId())
        .update();
  }
}
