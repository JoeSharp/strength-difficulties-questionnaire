package uk.ratracejoe.sdq.service;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import uk.ratracejoe.sdq.ClientFileParser;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.ParsedFile;
import uk.ratracejoe.sdq.model.sdq.SdqReportingPeriod;
import uk.ratracejoe.sdq.repository.*;

@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {
  private final InterventionRepository interventionRepository;
  private final DisabilityTypeRepository disabilityTypeRepository;
  private final ClientRepository fileRepository;
  private final ReportingPeriodRepository reportingPeriodRepository;
  private final SdqRepository sdqRepository;
  private final GoalRepository goalRepository;
  private final GboRepository gboRepository;
  private final ClientFileParser fileParser;

  public ParsedFile ingestFile(String filename, InputStream file) throws SdqException {
    ParsedFile parsedFile = fileParser.parse(filename, file);
    UUID clientId = parsedFile.sdqClient().clientId();

    fileRepository.createClient(parsedFile.sdqClient());
    parsedFile.sdqClient().interventions().forEach(it -> interventionRepository.save(clientId, it));
    parsedFile
        .sdqClient()
        .disabilityTypes()
        .forEach(dt -> disabilityTypeRepository.save(clientId, dt));
    Optional.ofNullable(parsedFile.sdq())
        .ifPresent(
            sdq -> {
              sdq.stream().map(SdqReportingPeriod::period).forEach(reportingPeriodRepository::save);
              sdq.stream()
                  .map(SdqReportingPeriod::sdq)
                  .map(Map::entrySet)
                  .flatMap(Set::stream)
                  .map(Map.Entry::getValue)
                  .forEach(sdqRepository::save);
            });

    Optional.of(parsedFile.goals()).ifPresent(goals -> goals.forEach(goalRepository::save));
    Optional.ofNullable(parsedFile.gbo()).ifPresent(gbo -> gbo.forEach(gboRepository::save));

    return parsedFile;
  }
}
