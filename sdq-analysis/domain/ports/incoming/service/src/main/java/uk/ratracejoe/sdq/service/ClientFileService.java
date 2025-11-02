package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.ClientFile;
import uk.ratracejoe.sdq.model.DemographicReport;

public interface ClientFileService {
  DemographicReport getDemographicReport(String demographic);

  List<ClientFile> getAll() throws SdqException;

  Optional<ClientFile> getByUUID(UUID uuid) throws SdqException;
}
