package uk.ratracejoe.sdq.repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.SdqClient;
import uk.ratracejoe.sdq.model.demographics.DemographicField;
import uk.ratracejoe.sdq.model.demographics.DemographicReport;

public interface ClientRepository {
  List<SdqClient> getAll() throws SdqException;

  List<SdqClient> getFiltered(Map<DemographicField, String> filters) throws SdqException;

  SdqClient get(UUID fileId) throws SdqException;

  SdqClient createClient(SdqClient sdqClient) throws SdqException;

  DemographicReport getDemographicReport(DemographicField demographic);

  int deleteAll();

  int update(SdqClient client);
}
