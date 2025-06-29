package uk.ratracejoe.sdq_analysis.rest;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.ratracejoe.sdq_analysis.dto.ParsedFile;
import uk.ratracejoe.sdq_analysis.dto.SdqPeriod;
import uk.ratracejoe.sdq_analysis.dto.SdqScores;
import uk.ratracejoe.sdq_analysis.dto.UploadFile;
import uk.ratracejoe.sdq_analysis.exception.SdqException;
import uk.ratracejoe.sdq_analysis.repository.SdqResponseRepository;
import uk.ratracejoe.sdq_analysis.repository.UploadFileRepository;
import uk.ratracejoe.sdq_analysis.service.XslDemographicExtractor;
import uk.ratracejoe.sdq_analysis.service.XslSdqExtractor;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadFileController {
    private final UploadFileRepository fileRepository;
    private final SdqResponseRepository sdqResponseRepository;
    private final XslSdqExtractor xslSdqExtractor;
    private final XslDemographicExtractor xslDemographicExtractor;

    @GetMapping
    public List<UploadFile> getAll() throws SdqException {
        return fileRepository
                .getAll();
    }

    @GetMapping("/scores")
    public List<SdqScores> getScores() throws SdqException {
        return sdqResponseRepository.getScores();
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<UploadFile> getByUUID(@PathVariable("uuid") UUID uuid) throws SdqException {
        return fileRepository
                .getByUUID(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ParsedFile uploadFile(@RequestParam("file") MultipartFile file) throws SdqException {
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            UploadFile uploadFile = xslDemographicExtractor.parse(workbook, file.getOriginalFilename());
            fileRepository.saveFile(uploadFile);
            List<SdqPeriod> periods = xslSdqExtractor.parse(workbook);
            sdqResponseRepository.recordResponse(uploadFile, periods);
            return new ParsedFile(uploadFile, periods);
        } catch (IOException e) {
            throw new SdqException("Could not parse workbook");
        }
    }
}