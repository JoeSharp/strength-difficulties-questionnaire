package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.DemographicField;
import uk.ratracejoe.sdq.model.DemographicReport;
import uk.ratracejoe.sdq.model.SdqClient;

public interface SdqClientService {
  SdqClient create(SdqClient newClient);

  DemographicReport getDemographicReport(DemographicField demographic);

  List<SdqClient> getAll() throws SdqException;

  List<SdqClient> getFiltered(Map<DemographicField, String> filters) throws SdqException;

  Optional<SdqClient> getByUUID(UUID uuid) throws SdqException;
}
