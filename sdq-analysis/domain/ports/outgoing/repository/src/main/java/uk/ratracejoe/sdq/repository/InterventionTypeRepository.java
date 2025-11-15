package uk.ratracejoe.sdq.repository;

import java.util.UUID;

public interface InterventionTypeRepository {
  void save(UUID fileId, String interventionType);
}
