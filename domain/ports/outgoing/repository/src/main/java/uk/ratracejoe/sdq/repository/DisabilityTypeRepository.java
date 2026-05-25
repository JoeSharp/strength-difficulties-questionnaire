package uk.ratracejoe.sdq.repository;

import java.util.List;
import java.util.UUID;
import uk.ratracejoe.sdq.model.demographics.DisabilityType;

public interface DisabilityTypeRepository {
  void save(UUID clientId, DisabilityType disabilityType);

  List<DisabilityType> getForClient(UUID clientId);

  int deleteAll();

  int deleteForClient(UUID clientId);
}
