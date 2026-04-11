package uk.ratracejoe.sdq.repository;

import java.util.List;
import java.util.UUID;
import uk.ratracejoe.sdq.model.demographics.InterventionType;

public interface InterventionTypeRepository {
  void save(UUID clientId, InterventionType interventionType);

  List<InterventionType> getForClient(UUID clientId);

  int deleteAll();

  int deleteForClient(UUID clientId);
}
