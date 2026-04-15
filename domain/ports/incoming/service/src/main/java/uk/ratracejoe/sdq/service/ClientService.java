package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.UUID;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.SdqClient;
import uk.ratracejoe.sdq.model.demographics.DemographicField;
import uk.ratracejoe.sdq.model.demographics.DemographicFilter;
import uk.ratracejoe.sdq.model.demographics.DemographicReport;

public interface ClientService {
  SdqClient create(SdqClient newClient);

  DemographicReport getDemographicReport(DemographicField demographic);

  List<SdqClient> getAll() throws SdqException;

  List<SdqClient> getFiltered(List<DemographicFilter> filters) throws SdqException;

  SdqClient getByUUID(UUID uuid) throws SdqException;

  SdqClient update(SdqClient client);
}
