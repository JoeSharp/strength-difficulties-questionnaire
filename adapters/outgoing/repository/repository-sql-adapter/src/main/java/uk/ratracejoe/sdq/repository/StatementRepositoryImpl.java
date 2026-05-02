package uk.ratracejoe.sdq.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.model.sdq.Category;
import uk.ratracejoe.sdq.model.sdq.Posture;
import uk.ratracejoe.sdq.model.sdq.Statement;

@RequiredArgsConstructor
public class StatementRepositoryImpl implements StatementRepository {
  private final JdbcClient jdbcClient;

  public Statement getStatement(String key) {
    return jdbcClient
        .sql(
            """
     SELECT
         s.order as order,
         s.statement_key as statement_key,
         s.category as category,
         c.posture as posture,
         s.description as description,
         s.is_true_positive as is_true_positive
     FROM
         sdq_statement s
     INNER JOIN sdq_category c
     ON s.category = c.category
     WHERE s.statement_key = :statement_key
     """)
        .param("statement_key", key)
        .query((rs, rowNum) -> statementFromRs(rs))
        .single();
  }

  @Override
  public List<Statement> getStatements() {
    return jdbcClient
        .sql(
            """
                SELECT
                s.order as order,
                    s.statement_key as statement_key,
                    s.category as category,
                    c.posture as posture,
                    s.description as description,
                    s.is_true_positive as is_true_positive
                FROM
                    sdq_statement s
                INNER JOIN sdq_category c
                ON s.category = c.category
                ORDER BY s.order ASC
                """)
        .query((rs, rowNum) -> statementFromRs(rs))
        .list();
  }

  @Override
  public List<Category> getCategories() {
    return jdbcClient
        .sql("SELECT category, posture FROM sdq_category")
        .query((rs, rowNum) -> categoryFromRs(rs))
        .list();
  }

  public static Statement statementFromRs(ResultSet rs) throws SQLException {
    return new Statement(
        rs.getInt("order"),
        rs.getString("statement_key"),
        categoryFromRs(rs),
        rs.getString("description"),
        rs.getBoolean("is_true_positive"));
  }

  public static Category categoryFromRs(ResultSet rs) throws SQLException {
    return new Category(rs.getString("category"), Posture.valueOf(rs.getString("posture")));
  }
}
