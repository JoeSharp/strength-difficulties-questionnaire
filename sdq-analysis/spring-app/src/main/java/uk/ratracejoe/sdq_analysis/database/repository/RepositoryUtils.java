package uk.ratracejoe.sdq_analysis.database.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ratracejoe.sdq_analysis.exception.SdqException;

public abstract class RepositoryUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryUtils.class);

  public static <R> R handle(DataSource dataSource, String operation, String sql, SqlFunction<R> fn)
      throws SdqException {
    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        return fn.apply(stmt);
      }
    } catch (SQLException e) {
      LOGGER.error(
          String.format("Could not execute %s on database %s", operation, e.getLocalizedMessage()),
          e);
      throw new SdqException(String.format("Could not execute %s on database", operation));
    }
  }

  public static Instant toInstant(Date sqlDate) {
    LocalDate localDate = sqlDate.toLocalDate();
    ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
    return zonedDateTime.toInstant();
  }

  public interface SqlFunction<R> {
    R apply(PreparedStatement stmt) throws SQLException;
  }
}
