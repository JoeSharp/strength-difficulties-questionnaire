package uk.ratracejoe.sdq.repository;

import java.util.UUID;
import uk.ratracejoe.sdq.model.InterventionType;

public interface InterventionTypeRepository {
  void save(UUID fileId, InterventionType interventionType);

  int deleteAll();
}
