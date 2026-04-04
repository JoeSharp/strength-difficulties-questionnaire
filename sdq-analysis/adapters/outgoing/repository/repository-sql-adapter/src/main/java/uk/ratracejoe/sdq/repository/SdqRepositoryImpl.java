package uk.ratracejoe.sdq.repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.*;
import uk.ratracejoe.sdq.tables.SdqTable;

@RequiredArgsConstructor
public class SdqRepositoryImpl implements SdqRepository {
  private final JdbcClient jdbcClient;

  @Override
  public void recordResponse(SdqSubmission sdq) throws SdqException {
    sdq.scores()
        .forEach(
            score -> {
              AtomicInteger paramIndex = new AtomicInteger(1);
              jdbcClient
                  .sql(SdqTable.insertSQL())
                  .param(paramIndex.getAndIncrement(), sdq.clientId())
                  .param(paramIndex.getAndIncrement(), sdq.period())
                  .param(paramIndex.getAndIncrement(), sdq.assessor().name())
                  .param(paramIndex.getAndIncrement(), score.statement().name())
                  .param(paramIndex.getAndIncrement(), score.statement().category().name())
                  .param(
                      paramIndex.getAndIncrement(), score.statement().category().posture().name())
                  .param(paramIndex.getAndIncrement(), score.score())
                  .update();
            });
  }

  public int deleteAll() throws SdqException {
    return jdbcClient.sql(SdqTable.deleteAllSQL()).update();
  }
}
