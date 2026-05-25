package uk.ratracejoe.sdq.repository;

import java.util.List;
import java.util.UUID;
import uk.ratracejoe.sdq.model.demographics.Intervention;

public interface InterventionRepository {
  void save(UUID clientId, Intervention intervention);

  List<Intervention> getForClient(UUID clientId);

  int deleteAll();

  int deleteForClient(UUID clientId);
}
