package uk.ratracejoe.sdq.repository;

import uk.ratracejoe.sdq.model.GboSubmission;

public interface GboRepository {
  void save(GboSubmission domain);

  int deleteAll();
}
