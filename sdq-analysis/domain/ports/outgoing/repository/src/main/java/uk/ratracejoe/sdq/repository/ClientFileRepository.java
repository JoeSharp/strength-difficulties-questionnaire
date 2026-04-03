package uk.ratracejoe.sdq.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.DemographicField;
import uk.ratracejoe.sdq.model.DemographicReport;
import uk.ratracejoe.sdq.model.SdqClient;

public interface ClientFileRepository {
  List<SdqClient> getAll() throws SdqException;

  List<SdqClient> getFiltered(Map<DemographicField, String> filters) throws SdqException;

  Optional<SdqClient> getByUUID(UUID fileId) throws SdqException;

  void saveFile(SdqClient sdqClient) throws SdqException;

  DemographicReport getDemographicReport(DemographicField demographic);
}
