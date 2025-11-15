package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.ClientFile;
import uk.ratracejoe.sdq.model.DemographicField;
import uk.ratracejoe.sdq.model.DemographicReport;

public interface ClientFileService {
  DemographicReport getDemographicReport(DemographicField demographic);

  List<ClientFile> getAll() throws SdqException;

  List<ClientFile> getFiltered(Map<DemographicField, String> filters) throws SdqException;

  Optional<ClientFile> getByUUID(UUID uuid) throws SdqException;
}
