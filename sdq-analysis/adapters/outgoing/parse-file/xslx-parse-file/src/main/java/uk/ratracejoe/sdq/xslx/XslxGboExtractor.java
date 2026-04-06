package uk.ratracejoe.sdq.xslx;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.GboParsedPeriod;
import uk.ratracejoe.sdq.model.GboParsedScore;

public class XslxGboExtractor {

  private static final String SHEET_NAME = "Goal Based Outcomes (GBO)";
  public static final int NUMBER_SCORES_EXPECTED = 6;
  private static final int NUMBER_PERIODS_EXPECTED = 18;
  private static final int FIRST_SCORE_COLUMN = 4;
  private static final List<AssessorRow> FIRST_ROWS =
      List.of(
          new AssessorRow(Assessor.Parent1, 12),
          new AssessorRow(Assessor.Parent2, 35),
          new AssessorRow(Assessor.School, 58),
          new AssessorRow(Assessor.Child, 81));

  public List<GboParsedPeriod> parse(Workbook workbook) {
    return StreamSupport.stream(workbook.spliterator(), false)
        .filter(this::isGBO)
        .findFirst()
        .map(sheet -> this.parseGbo(sheet))
        .orElseThrow(() -> new SdqException("Could not find Goal Based Outcomes Sheet"));
  }

  private boolean isGBO(Sheet sheet) {
    return sheet.getSheetName().equals(SHEET_NAME);
  }

  private List<GboParsedPeriod> parseGbo(Sheet sheet) {
    return FIRST_ROWS.stream()
        .flatMap(d -> getGboPeriods(d.assessor(), sheet, d.firstRow()))
        .toList();
  }

  private Stream<GboParsedPeriod> getGboPeriods(Assessor assessor, Sheet sheet, int startRow) {
    return IntStream.range(1, NUMBER_PERIODS_EXPECTED + 1)
        .mapToObj(i -> extractPeriod(assessor, sheet.getRow(startRow + i - 1)));
  }

  private GboParsedPeriod extractPeriod(Assessor assessor, Row row) {
    Instant periodDate = extractDate(row);
    List<GboParsedScore> scores =
        extractScores(row).entrySet().stream()
            .map(e -> GboParsedScore.builder().index(e.getKey()).score(e.getValue()).build())
            .toList();

    return GboParsedPeriod.builder().period(periodDate).assessor(assessor).scores(scores).build();
  }

  private Instant extractDate(Row row) {
    return Optional.ofNullable(row.getCell(FIRST_SCORE_COLUMN - 1))
        .map(Cell::getDateCellValue)
        .map(Date::toInstant)
        .orElse(Instant.now());
  }

  private Map<Integer, Integer> extractScores(Row row) {
    return IntStream.range(1, NUMBER_SCORES_EXPECTED + 1)
        .boxed()
        .collect(Collectors.toMap(Function.identity(), d -> getScore(row, d)));
  }

  private Integer getScore(Row row, int scoreIndex) {
    return Optional.ofNullable(row.getCell(FIRST_SCORE_COLUMN + scoreIndex - 1))
        .map(Cell::getNumericCellValue)
        .map(Double::intValue)
        .orElse(0);
  }
}
