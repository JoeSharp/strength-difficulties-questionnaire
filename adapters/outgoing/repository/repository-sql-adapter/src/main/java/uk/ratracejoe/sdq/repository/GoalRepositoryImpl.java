package uk.ratracejoe.sdq.repository;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.model.gbo.Goal;

@RequiredArgsConstructor
public class GoalRepositoryImpl implements GoalRepository {
  private static final String TABLE_NAME = "goal";
  private static final String FIELD_CLIENT_ID = "client_id";
  private static final String FIELD_GOAL_ID = "goal_id";
  private static final String FIELD_DESCRIPTION = "description";

  private final JdbcClient jdbcClient;

  @Override
  public void save(Goal goal) {
    jdbcClient
        .sql(
            String.format(
                "INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)",
                TABLE_NAME, FIELD_CLIENT_ID, FIELD_GOAL_ID, FIELD_DESCRIPTION))
        .param(1, goal.clientId())
        .param(2, goal.goalId())
        .param(3, goal.description())
        .update();
  }

  @Override
  public Goal get(UUID goalId) {
    return jdbcClient
        .sql(
            String.format(
                "SELECT %s, %s FROM %s WHERE %s = ?",
                FIELD_CLIENT_ID, FIELD_DESCRIPTION, TABLE_NAME, FIELD_GOAL_ID))
        .param(1, goalId)
        .query(
            (rs, rowNum) ->
                Goal.builder()
                    .goalId(goalId)
                    .clientId(rs.getObject(FIELD_CLIENT_ID, UUID.class))
                    .description(rs.getString(FIELD_DESCRIPTION))
                    .build())
        .single();
  }

  @Override
  public List<Goal> getForClient(UUID clientId) {
    return jdbcClient
        .sql(
            String.format(
                "SELECT %s, %s FROM %s WHERE %s = ?",
                FIELD_GOAL_ID, FIELD_DESCRIPTION, TABLE_NAME, FIELD_CLIENT_ID))
        .param(1, clientId)
        .query(
            (rs, rowNum) ->
                Goal.builder()
                    .clientId(clientId)
                    .goalId(rs.getObject(FIELD_GOAL_ID, UUID.class))
                    .description(rs.getString(FIELD_DESCRIPTION))
                    .build())
        .list();
  }

  @Override
  public int deleteAll() {
    return jdbcClient.sql(String.format("DELETE FROM %s", TABLE_NAME)).update();
  }

  @Override
  public int update(Goal goal) {
    return jdbcClient
        .sql(
            String.format(
                "UPDATE %s SET %s = ? WHERE %s = ?", TABLE_NAME, FIELD_DESCRIPTION, FIELD_GOAL_ID))
        .param(1, goal.description())
        .param(2, goal.goalId())
        .update();
  }
}
