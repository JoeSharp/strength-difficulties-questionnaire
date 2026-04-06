package uk.ratracejoe.sdq.xslx;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.poi.ss.usermodel.*;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.GboParsedPeriod;
import uk.ratracejoe.sdq.model.GboParsedScore;

public class WorkbookGboExtractor {

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
        .map(this::parseGbo)
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
        .mapToObj(i -> extractPeriod(assessor, sheet.getRow(startRow + i - 1)))
        .filter(Objects::nonNull);
  }

  private GboParsedPeriod extractPeriod(Assessor assessor, Row row) {
    LocalDate periodDate = extractDate(row);
    if (Objects.isNull(periodDate)) return null;
    List<GboParsedScore> scores = extractScores(row);

    return GboParsedPeriod.builder().period(periodDate).assessor(assessor).scores(scores).build();
  }

  private LocalDate extractDate(Row row) {
    return Optional.ofNullable(row.getCell(FIRST_SCORE_COLUMN - 1))
            .filter(c -> c.getCellType() != CellType.BLANK)
            .map(Cell::getLocalDateTimeCellValue)
            .map(LocalDateTime::toLocalDate)
            .orElse(null);
  }

  private List<GboParsedScore> extractScores(Row row) {
    List<GboParsedScore> scores = new ArrayList<>();
    for (int i = 0; i < NUMBER_SCORES_EXPECTED; i++) {
      int index = i + 1;
      Optional.ofNullable(getScore(row, i))
          .ifPresent(
              score -> scores.add(GboParsedScore.builder().index(index).score(score).build()));
    }
    return scores;
  }

  private Integer getScore(Row row, int scoreIndex) {
    Cell cell = row.getCell(FIRST_SCORE_COLUMN + scoreIndex);
    if (Objects.isNull(cell) || cell.getCellType() == CellType.BLANK) {
      return null;
    }
    double score = cell.getNumericCellValue();
    return (int) score;
  }
}
