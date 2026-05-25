package uk.ratracejoe.sdq.repository;

import java.util.Map;
import java.util.UUID;
import uk.ratracejoe.sdq.model.demographics.AceType;

public interface AcesRepository {
  void save(UUID clientId, AceType intervention, Integer score);

  Map<AceType, Integer> getForClient(UUID clientId);

  int deleteAll();

  int deleteForClient(UUID clientId);
}
