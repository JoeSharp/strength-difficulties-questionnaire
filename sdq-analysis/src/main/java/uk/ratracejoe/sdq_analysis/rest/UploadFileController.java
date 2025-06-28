package uk.ratracejoe.sdq_analysis.rest;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.ratracejoe.sdq_analysis.dto.SdqPeriod;
import uk.ratracejoe.sdq_analysis.dto.UploadFile;
import uk.ratracejoe.sdq_analysis.exception.SdqException;
import uk.ratracejoe.sdq_analysis.repository.UploadFileRepository;
import uk.ratracejoe.sdq_analysis.service.XslSdqExtractor;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadFileController {
    private final UploadFileRepository fileRepository;
    private final XslSdqExtractor xslSdqExtractor;

    @GetMapping
    public List<UploadFile> getAll() throws SdqException {
        return fileRepository
                .getAll();
    }

    @PostMapping
    public List<SdqPeriod> uploadFile(@RequestParam("file") MultipartFile file) throws SdqException {
        try {
            UploadFile uploadFile = new UploadFile(UUID.randomUUID(), file.getOriginalFilename());
            fileRepository.saveFile(uploadFile);
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            return xslSdqExtractor.parse(workbook);
        } catch (IOException e) {
            throw new SdqException("Could not parse workbook");
        }
    }
}