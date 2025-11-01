package uk.ratracejoe.sdq_analysis.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq_analysis.database.entity.ClientFileEntity;
import uk.ratracejoe.sdq_analysis.database.entity.InterventionTypeEntity;
import uk.ratracejoe.sdq_analysis.database.repository.ClientFileRepository;
import uk.ratracejoe.sdq_analysis.database.repository.InterventionTypeRepository;
import uk.ratracejoe.sdq_analysis.dto.*;
import uk.ratracejoe.sdq_analysis.exception.SdqException;
import uk.ratracejoe.sdq_analysis.service.xslx.XslxDemographicExtractor;
import uk.ratracejoe.sdq_analysis.service.xslx.XslxGboExtractor;
import uk.ratracejoe.sdq_analysis.service.xslx.XslxSdqExtractor;
import uk.ratracejoe.sdq_analysis.service.xslx.XslxStructureExtractor;

@Service
@RequiredArgsConstructor
public class UploadService {
  private final DatabaseService dbService;
  private final InterventionTypeRepository interventionTypeRepository;
  private final ClientFileRepository fileRepository;
  private final SdqService sdqService;
  private final GboService gboService;
  private final XslxSdqExtractor xslSdqExtractor;
  private final XslxGboExtractor xslxGboExtractor;
  private final XslxStructureExtractor structureExtractor;
  private final XslxDemographicExtractor xslDemographicExtractor;

  public ParsedFile ingestFile(String filename, InputStream file) throws IOException, SdqException {

    Workbook workbook = new XSSFWorkbook(file);
    var demographics = structureExtractor.extractDemographicOptions(workbook);
    SdqEnumerations structure = new SdqEnumerations(demographics);
    dbService.ensureEnumerations(structure);

    ClientFile clientFile = xslDemographicExtractor.parse(workbook, filename);
    ClientFileEntity clientFileEntity =
        new ClientFileEntity(
            clientFile.uuid(),
            clientFile.filename(),
            clientFile.dateOfBirth(),
            clientFile.gender(),
            clientFile.council(),
            clientFile.ethnicity(),
            clientFile.englishAdditionalLanguage(),
            clientFile.disabilityStatus(),
            clientFile.disabilityType(),
            clientFile.careExperience(),
            clientFile.aces(),
            clientFile.fundingSource());
    fileRepository.saveFile(clientFileEntity);
    clientFile.interventionTypes().stream()
        .map(it -> new InterventionTypeEntity(clientFile.uuid(), it))
        .forEach(interventionTypeRepository::save);
    List<SdqPeriod> sdqs = xslSdqExtractor.parse(workbook);
    sdqService.recordResponse(clientFile, sdqs);
    Map<Assessor, List<GboPeriod>> gbos = xslxGboExtractor.parse(workbook);
    gboService.recordResponse(clientFile, gbos);

    return new ParsedFile(clientFile, sdqs, gbos);
  }
}
