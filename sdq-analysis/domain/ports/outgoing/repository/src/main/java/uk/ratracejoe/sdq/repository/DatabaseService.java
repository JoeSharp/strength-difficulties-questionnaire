package uk.ratracejoe.sdq.repository;

import uk.ratracejoe.sdq.model.SdqEnumerations;

public interface DatabaseService {
  boolean databaseExists();

  void ensureEnumerations(SdqEnumerations sdqEnumerations);
}
