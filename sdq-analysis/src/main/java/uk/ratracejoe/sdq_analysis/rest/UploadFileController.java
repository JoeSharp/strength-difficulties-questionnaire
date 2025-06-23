package uk.ratracejoe.sdq_analysis.rest;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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

    @GetMapping
    public List<String> ingestedFilenames() {
        return fileRepository
                .findAll().stream()
                .map(IngestedFile::getFilename)
                .toList();
    }

    private static final String SHEET_NAME_PREFIX = "SDQ Period";
    private static final int FIRST_ROW_PARENT = 6;
    private static final int FIRST_ROW_SCHOOL = 41;
    private static final int FIRST_ROW_CHILD = 70;

    private boolean isSDQ(Sheet sheet) {
        return sheet.getSheetName().startsWith(SHEET_NAME_PREFIX);
    }

    private int countScore(Sheet sheet, int row) {
        return Stream.of(4, 5, 6)
                .map(sheet.getRow(row)::getCell)
                .map(Cell::getNumericCellValue)
                .map(Double::intValue)
                .reduce(0, Integer::sum);
    }

    private List<StatementResponse> getStatementResponse(Sheet sheet, int startRow) {
        return IntStream.range(0, Statement.values().length - 1)
                .mapToObj(i -> new StatementResponse(Statement.values()[i], countScore(sheet, i + startRow)))
                .toList();
    }
    private SdqPeriod parseSdqPeriod(Sheet sheet) {
        Integer periodIndex = Integer.parseInt(
                sheet.getSheetName()
                        .replace(SHEET_NAME_PREFIX, "").trim(),
                10);

        Map<Assessor, List<StatementResponse>> responses = Map.of(
                Assessor.Parent, getStatementResponse(sheet, FIRST_ROW_PARENT),
                Assessor.School, getStatementResponse(sheet, FIRST_ROW_SCHOOL),
                Assessor.Child, getStatementResponse(sheet, FIRST_ROW_CHILD)
        );

        return new SdqPeriod(periodIndex, responses);

    }

    @PostMapping
    public List<SdqPeriod> ingestFile(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        List<SdqPeriod> periods = StreamSupport.stream(workbook.spliterator(), false)
                .filter(this::isSDQ)
                .map(this::parseSdqPeriod)
                .toList();
        return periods;

        /*
        var newFile = new IngestedFile(UUID.randomUUID(), "foo");
        fileRepository.save(newFile);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");
        return new RedirectView("/index.html");
         */
    }
}