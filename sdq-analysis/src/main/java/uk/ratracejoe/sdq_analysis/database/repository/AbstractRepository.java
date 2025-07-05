package uk.ratracejoe.sdq_analysis.database.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ratracejoe.sdq_analysis.exception.SdqException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class AbstractRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRepository.class);

    public static <R> R handle(DataSource dataSource,
                               String operation,
                               String sql,
                               SqlFunction<R> fn) throws SdqException {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                return fn.apply(stmt);
            }
        } catch (SQLException e) {
            LOGGER.error("Could not execute {} on database {}",
                    operation,
                    e.getLocalizedMessage());
            throw new SdqException(String.format("Could not execute %s on database", operation));
        }
    }

    public interface SqlFunction<R> {
        R apply(PreparedStatement stmt) throws SQLException;
    }
}
