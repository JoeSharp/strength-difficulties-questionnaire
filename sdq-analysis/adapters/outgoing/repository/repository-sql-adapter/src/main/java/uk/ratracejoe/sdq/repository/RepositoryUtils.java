package uk.ratracejoe.sdq.repository;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public abstract class RepositoryUtils {
  public static Instant toInstant(Date sqlDate) {
    LocalDate localDate = sqlDate.toLocalDate();
    ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
    return zonedDateTime.toInstant();
  }
}
