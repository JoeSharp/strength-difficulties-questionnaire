package uk.ratracejoe.sdq_analysis.rest;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ratracejoe.sdq_analysis.dto.Assessor;
import uk.ratracejoe.sdq_analysis.dto.SdqPeriod;
import uk.ratracejoe.sdq_analysis.dto.Statement;
import uk.ratracejoe.sdq_analysis.dto.StatementResponse;
import uk.ratracejoe.sdq_analysis.repository.IngestedFileRepository;
import uk.ratracejoe.sdq_analysis.repository.entity.IngestedFile;
import uk.ratracejoe.sdq_analysis.service.XslSdqExtractor;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadFileController {
    private final IngestedFileRepository fileRepository;
    private final XslSdqExtractor xslSdqExtractor;

    @GetMapping
    public List<String> ingestedFilenames() {
        return fileRepository
                .findAll().stream()
                .map(IngestedFile::getFilename)
                .toList();
    }

    @PostMapping
    public List<SdqPeriod> ingestFile(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        return xslSdqExtractor.parse(workbook);
    }
}