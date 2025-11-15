package uk.ratracejoe.sdq.repository;

import java.util.List;
import java.util.UUID;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.GboScore;

public interface GboRepository {
  List<GboScore> getByFileUuid(UUID uuid) throws SdqException;

  void save(GboScore domain);
}
