package uk.ratracejoe.sdq_analysis.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq_analysis.dto.Assessor;
import uk.ratracejoe.sdq_analysis.dto.SdqPeriod;
import uk.ratracejoe.sdq_analysis.dto.Statement;
import uk.ratracejoe.sdq_analysis.dto.StatementResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class XslSdqExtractor {
    private static final Logger LOGGER = getLogger(XslSdqExtractor.class);

    private static final String SHEET_NAME_PREFIX = "SDQ Period";
    private static final int[] COL_SCORES = {4, 5, 6};
    private static final int FIRST_ROW_PARENT = 6;
    private static final int FIRST_ROW_SCHOOL = 40;
    private static final int FIRST_ROW_CHILD = 70;

    public List<SdqPeriod> parse(Workbook workbook) {
        return StreamSupport.stream(workbook.spliterator(), false)
                .filter(this::isSDQ)
                .map(this::parseSdqPeriod)
                .toList();
    }

    private boolean isSDQ(Sheet sheet) {
        return sheet.getSheetName().startsWith(SHEET_NAME_PREFIX);
    }

    private int countScore(Sheet sheet, int rowIndex) {
        Row row = sheet.getRow(rowIndex);
        if (Objects.isNull(row)) {
            LOGGER.warn("Could not get row {}", rowIndex);
            return 0;
        }

        return Arrays.stream(COL_SCORES)
                .mapToObj(row::getCell)
                .map(Cell::getNumericCellValue)
                .map(Double::intValue)
                .reduce(0, Integer::sum);
    }

    private StatementResponse getStatementResponse(Sheet sheet, Statement statement, int row) {
        return new StatementResponse(statement, countScore(sheet, row));
    }

    private List<StatementResponse> getStatementResponses(Sheet sheet, int startRow) {
        return IntStream.range(0, Statement.values().length - 1)
                .mapToObj(i -> getStatementResponse(sheet, Statement.values()[i], i + startRow))
                .toList();
    }

    private SdqPeriod parseSdqPeriod(Sheet sheet) {
        Integer periodIndex = Integer.parseInt(
                sheet.getSheetName()
                        .replace(SHEET_NAME_PREFIX, "").trim(),
                10);

        Map<Assessor, List<StatementResponse>> responses = Map.of(
                Assessor.Parent, getStatementResponses(sheet, FIRST_ROW_PARENT),
                Assessor.School, getStatementResponses(sheet, FIRST_ROW_SCHOOL),
                Assessor.Child, getStatementResponses(sheet, FIRST_ROW_CHILD)
        );

        return new SdqPeriod(periodIndex, responses);
    }
}
