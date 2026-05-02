package uk.ratracejoe.sdq.xslx;

import static org.slf4j.LoggerFactory.getLogger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.ReportingPeriod;
import uk.ratracejoe.sdq.model.sdq.*;

public class WorkbookSdqExtractor {
  private static final Logger LOGGER = getLogger(WorkbookSdqExtractor.class);

  private static final String SHEET_NAME_PREFIX = "SDQ Period";
  private static final Integer INVALID_SCORE = -1;
  private static final int[] COL_SCORES = {4, 5, 6};

  private static final List<AssessorRow> FIRST_ROWS =
      List.of(
          new AssessorRow(Assessor.Parent1, 11),
          new AssessorRow(Assessor.Parent2, 44),
          new AssessorRow(Assessor.School, 75),
          new AssessorRow(Assessor.Child, 105));

  public List<SdqReportingPeriod> parse(UUID clientId, Workbook workbook) {
    return StreamSupport.stream(workbook.spliterator(), false)
        .filter(this::isSDQ)
        .map(sheet -> this.parseSdqPeriod(clientId, sheet))
        .filter(this::isPopulated)
        .toList();
  }

  private boolean isPopulated(SdqReportingPeriod period) {
    return !period.sdq().values().stream()
        .map(SdqSubmission::scores)
        .flatMap(List::stream)
        .map(SdqScore::score)
        .allMatch(INVALID_SCORE::equals);
  }

  private boolean isSDQ(Sheet sheet) {
    return sheet.getSheetName().startsWith(SHEET_NAME_PREFIX);
  }

  private int countScore(Sheet sheet, int rowIndex) {
    Row row = sheet.getRow(rowIndex);
    if (Objects.isNull(row)) {
      LOGGER.warn("Could not get row {}", rowIndex);
      return INVALID_SCORE;
    }

    boolean allBlank =
        Arrays.stream(COL_SCORES)
            .mapToObj(row::getCell)
            .allMatch(c -> CellType.BLANK.equals(c.getCellType()));
    if (allBlank) {
      return INVALID_SCORE;
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
    return IntStream.range(0, Statement.values().length)
        .mapToObj(i -> getStatementResponse(sheet, Statement.values()[i], i + startRow))
        .toList();
  }

  private SdqReportingPeriod parseSdqPeriod(UUID clientId, Sheet sheet) {
    UUID periodId = UUID.randomUUID();
    Map<Assessor, SdqSubmission> sdq = new EnumMap<>(Assessor.class);

    for (var firstRow : FIRST_ROWS) {
      List<SdqScore> scores =
          getStatementResponses(sheet, firstRow.firstRow()).stream()
              .map(r -> new SdqScore(r.statement(), r.score()))
              .toList();
      SdqSubmission submission =
          SdqSubmission.builder()
              .periodId(periodId)
              .assessor(firstRow.assessor())
              .scores(scores)
              .build();
      sdq.put(firstRow.assessor(), submission);
    }
    return SdqReportingPeriod.builder()
        .period(
            ReportingPeriod.builder()
                .periodId(periodId)
                .period(findPeriodDate(sheet))
                .clientId(clientId)
                .build())
        .sdq(sdq)
        .build();
  }

  private LocalDate findPeriodDate(Sheet sheet) {
    return Optional.ofNullable(sheet.getRow(5))
        .map(row -> row.getCell(3))
        .map(Cell::getLocalDateTimeCellValue)
        .map(LocalDateTime::toLocalDate)
        .orElseGet(LocalDate::now);
  }
}
