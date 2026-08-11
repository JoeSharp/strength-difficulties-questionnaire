package uk.ratracejoe.sdq.repository;

import uk.ratracejoe.sdq.model.gbo.GboSubmission;

public interface GboRepository {
  void save(GboSubmission domain);

  int deleteAll();
}
