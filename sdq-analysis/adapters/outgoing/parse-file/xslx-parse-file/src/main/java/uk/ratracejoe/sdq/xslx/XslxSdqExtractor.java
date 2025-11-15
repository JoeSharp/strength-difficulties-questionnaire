package uk.ratracejoe.sdq.xslx;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.SdqScore;
import uk.ratracejoe.sdq.model.Statement;
import uk.ratracejoe.sdq.model.StatementResponse;

public class XslxSdqExtractor {
  private static final Logger LOGGER = getLogger(XslxSdqExtractor.class);

  private static final String SHEET_NAME_PREFIX = "SDQ Period";
  private static final int[] COL_SCORES = {4, 5, 6};

  private static final List<AssessorRow> FIRST_ROWS =
      List.of(
          new AssessorRow(Assessor.Parent1, 11),
          new AssessorRow(Assessor.Parent2, 44),
          new AssessorRow(Assessor.School, 75),
          new AssessorRow(Assessor.Child, 105));

  public List<SdqScore> parse(UUID fileId, Workbook workbook) {
    return StreamSupport.stream(workbook.spliterator(), false)
        .filter(this::isSDQ)
        .flatMap(sheet -> this.parseSdqPeriod(fileId, sheet))
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

  private Stream<SdqScore> parseSdqPeriod(UUID fileId, Sheet sheet) {
    Integer periodIndex =
        Integer.parseInt(sheet.getSheetName().replace(SHEET_NAME_PREFIX, "").trim(), 10);

    return FIRST_ROWS.stream()
        .flatMap(
            a ->
                getStatementResponses(sheet, a.firstRow()).stream()
                    .map(
                        r ->
                            new SdqScore(
                                fileId, periodIndex, a.assessor(), r.statement(), r.score())));
  }
}
