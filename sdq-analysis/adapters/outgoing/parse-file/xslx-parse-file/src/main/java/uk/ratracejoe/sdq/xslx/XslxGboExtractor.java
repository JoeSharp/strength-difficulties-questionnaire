package uk.ratracejoe.sdq.xslx;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.GboPeriod;

public class XslxGboExtractor {

  private static final String SHEET_NAME = "Goal Based Outcomes (GBO)";
  public static final int NUMBER_SCORES_EXPECTED = 6;
  private static final int NUMBER_PERIODS_EXPECTED = 18;
  private static final int FIRST_SCORE_COLUMN = 4;
  private static final int FIRST_ROW_PARENT_1 = 12;
  private static final int FIRST_ROW_PARENT_2 = 35;
  private static final int FIRST_ROW_SCHOOL = 58;
  private static final int FIRST_ROW_CHILD = 81;

  public Map<Assessor, List<GboPeriod>> parse(Workbook workbook) {
    return StreamSupport.stream(workbook.spliterator(), false)
        .filter(this::isGBO)
        .findFirst()
        .map(this::parseGbo)
        .orElseThrow(() -> new SdqException("Could not find Goal Based Outcomes Sheet"));
  }

  private boolean isGBO(Sheet sheet) {
    return sheet.getSheetName().equals(SHEET_NAME);
  }

  private Integer getScore(Row row, int scoreIndex) {
    return Optional.ofNullable(row.getCell(FIRST_SCORE_COLUMN + scoreIndex - 1))
        .map(Cell::getNumericCellValue)
        .map(Double::intValue)
        .orElse(0);
  }

  private Map<Integer, Integer> extractScores(Row row) {
    return IntStream.range(1, NUMBER_SCORES_EXPECTED + 1)
        .boxed()
        .collect(Collectors.toMap(Function.identity(), d -> getScore(row, d)));
  }

  private Instant extractDate(Row row) {
    return Optional.ofNullable(row.getCell(FIRST_SCORE_COLUMN - 1))
        .map(Cell::getDateCellValue)
        .map(Date::toInstant)
        .orElse(Instant.now());
  }

  private GboPeriod extractPeriod(int periodIndex, Row row) {
    var scores = extractScores(row);
    var periodDate = extractDate(row);
    return new GboPeriod(periodIndex, periodDate, scores);
  }

  private List<GboPeriod> getGboPeriods(Sheet sheet, int startRow) {
    return IntStream.range(1, NUMBER_PERIODS_EXPECTED + 1)
        .mapToObj(i -> extractPeriod(i, sheet.getRow(startRow + i - 1)))
        .toList();
  }

  private Map<Assessor, List<GboPeriod>> parseGbo(Sheet sheet) {
    return Map.of(
        Assessor.Parent1, getGboPeriods(sheet, FIRST_ROW_PARENT_1),
        Assessor.Parent2, getGboPeriods(sheet, FIRST_ROW_PARENT_2),
        Assessor.School, getGboPeriods(sheet, FIRST_ROW_SCHOOL),
        Assessor.Child, getGboPeriods(sheet, FIRST_ROW_CHILD));
  }
}
