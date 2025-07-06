package uk.ratracejoe.sdq_analysis.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq_analysis.database.entity.ClientFileEntity;
import uk.ratracejoe.sdq_analysis.database.entity.InterventionTypeEntity;
import uk.ratracejoe.sdq_analysis.database.repository.ClientFileRepository;
import uk.ratracejoe.sdq_analysis.database.repository.InterventionTypeRepository;
import uk.ratracejoe.sdq_analysis.database.repository.SdqResponseRepository;
import uk.ratracejoe.sdq_analysis.dto.ClientFile;
import uk.ratracejoe.sdq_analysis.dto.ParsedFile;
import uk.ratracejoe.sdq_analysis.dto.SdqPeriod;
import uk.ratracejoe.sdq_analysis.exception.SdqException;
import uk.ratracejoe.sdq_analysis.service.xslx.XslxDemographicExtractor;
import uk.ratracejoe.sdq_analysis.service.xslx.XslxSdqExtractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UploadService {
    private final DatabaseService dbService;
    private final InterventionTypeRepository interventionTypeRepository;
    private final ClientFileRepository fileRepository;
    private final SdqResponseService sdqResponseService;
    private final XslxSdqExtractor xslSdqExtractor;
    private final XslxDemographicExtractor xslDemographicExtractor;

    public ParsedFile ingestFile(String filename, InputStream file) throws IOException, SdqException {
        if (!dbService.databaseExists()) throw new SdqException("DB Not ready");

        Workbook workbook = new XSSFWorkbook(file);
        ClientFile clientFile = xslDemographicExtractor.parse(workbook, filename);
        ClientFileEntity clientFileEntity = new ClientFileEntity(
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
                clientFile.fundingSource()
        );
        fileRepository.saveFile(clientFileEntity);
        clientFile.interventionTypes()
                .stream()
                .map(it -> new InterventionTypeEntity(clientFile.uuid(), it))
                .forEach(interventionTypeRepository::save
        );
        List<SdqPeriod> periods = xslSdqExtractor.parse(workbook);
        sdqResponseService.recordResponse(clientFile, periods);
        return new ParsedFile(clientFile, periods);

    }
}
