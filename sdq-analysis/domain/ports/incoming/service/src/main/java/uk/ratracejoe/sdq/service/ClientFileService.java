package uk.ratracejoe.sdq.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.ClientFile;

public interface ClientFileService {
  List<ClientFile> getAll() throws SdqException;

  Optional<ClientFile> getByUUID(UUID uuid) throws SdqException;
}
