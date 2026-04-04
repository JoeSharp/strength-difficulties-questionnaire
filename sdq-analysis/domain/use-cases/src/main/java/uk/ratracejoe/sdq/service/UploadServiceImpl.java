package uk.ratracejoe.sdq.service;

import java.io.InputStream;
import java.util.UUID;
import uk.ratracejoe.sdq.SdqFileParser;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.*;
import uk.ratracejoe.sdq.repository.*;

public class UploadServiceImpl implements UploadService {
  private final InterventionTypeRepository interventionTypeRepository;
  private final SdqClientRepository fileRepository;
  private final SdqService sdqService;
  private final GboService gboService;
  private final SdqFileParser fileParser;

  public UploadServiceImpl(
      InterventionTypeRepository interventionTypeRepository,
      SdqClientRepository fileRepository,
      SdqService sdqService,
      GboService gboService,
      SdqFileParser fileParser) {
    this.interventionTypeRepository = interventionTypeRepository;
    this.fileRepository = fileRepository;
    this.sdqService = sdqService;
    this.gboService = gboService;
    this.fileParser = fileParser;
  }

  public ParsedFile ingestFile(String filename, InputStream file) throws SdqException {
    ParsedFile parsedFile = fileParser.parse(filename, file);
    UUID clientId = parsedFile.sdqClient().clientId();

    fileRepository.createClient(parsedFile.sdqClient());
    parsedFile
        .sdqClient()
        .interventionTypes()
        .forEach(it -> interventionTypeRepository.save(clientId, it));
    parsedFile.sdq().forEach(sdqService::recordResponse);
    parsedFile.gbo().forEach(gboService::recordResponse);

    return parsedFile;
  }
}
