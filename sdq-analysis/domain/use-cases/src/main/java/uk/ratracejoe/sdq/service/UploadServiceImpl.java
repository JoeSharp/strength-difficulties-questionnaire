package uk.ratracejoe.sdq.service;

import java.io.InputStream;
import java.util.UUID;
import uk.ratracejoe.sdq.SdqFileParser;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.*;
import uk.ratracejoe.sdq.repository.*;

public class UploadServiceImpl implements UploadService {
  private final DemographicOptionRepository demographicOptionRepository;
  private final InterventionTypeRepository interventionTypeRepository;
  private final ClientFileRepository fileRepository;
  private final SdqService sdqService;
  private final GboService gboService;
  private final SdqFileParser fileParser;

  public UploadServiceImpl(
      DemographicOptionRepository demographicOptionRepository,
      InterventionTypeRepository interventionTypeRepository,
      ClientFileRepository fileRepository,
      SdqService sdqService,
      GboService gboService,
      SdqFileParser fileParser) {
    this.demographicOptionRepository = demographicOptionRepository;
    this.interventionTypeRepository = interventionTypeRepository;
    this.fileRepository = fileRepository;
    this.sdqService = sdqService;
    this.gboService = gboService;
    this.fileParser = fileParser;
  }

  public ParsedFile ingestFile(String filename, InputStream file) throws SdqException {
    ParsedFile parsedFile = fileParser.parse(filename, file);
    UUID fileId = parsedFile.clientFile().fileId();

    demographicOptionRepository.ensureEnumerations(parsedFile.structure());
    fileRepository.saveFile(parsedFile.clientFile());
    parsedFile
        .clientFile()
        .interventionTypes()
        .forEach(it -> interventionTypeRepository.save(fileId, it));
    sdqService.recordResponse(fileId, parsedFile.sdqPeriods());
    gboService.recordResponse(fileId, parsedFile.gboPeriods());

    return parsedFile;
  }
}
