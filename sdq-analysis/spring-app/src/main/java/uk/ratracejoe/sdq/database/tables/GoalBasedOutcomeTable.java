package uk.ratracejoe.sdq.database.tables;

import static uk.ratracejoe.sdq.service.xslx.XslxGboExtractor.NUMBER_SCORES_EXPECTED;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface GoalBasedOutcomeTable {
  String TABLE_NAME = "gbo";

  String FIELD_FILE_ID = "file_id";
  String FIELD_ASSESSOR = "assessor";
  String FIELD_PERIOD_INDEX = "period_index";
  String FIELD_PERIOD_DATE = "period_date";
  String FIELD_SCORE_INDEX = "score_index";
  String FIELD_SCORE = "score";

  static String getByFileSQL() {
    return String.format("SELECT * FROM %s WHERE %s = ?", TABLE_NAME, FIELD_FILE_ID);
  }

  static String pivotFieldForScore(int scoreIndex) {
    return String.format("period_%d", scoreIndex);
  }

  static String selectScoresPivotSQL() {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT ");
    String fieldList =
        String.join(",", FIELD_FILE_ID, FIELD_ASSESSOR, FIELD_PERIOD_INDEX, FIELD_PERIOD_DATE);
    sb.append(fieldList);
    sb.append(",");
    String scoresStr =
        IntStream.range(1, NUMBER_SCORES_EXPECTED + 1)
            .mapToObj(
                i ->
                    String.format(
                        "  SUM(CASE WHEN %s = '%s' THEN %s ELSE 0 END) AS %s",
                        FIELD_SCORE_INDEX, i, FIELD_SCORE, pivotFieldForScore(i)))
            .collect(Collectors.joining(","));
    sb.append(scoresStr);
    sb.append(" FROM ");
    sb.append(TABLE_NAME);
    sb.append(" WHERE ");
    sb.append(FIELD_FILE_ID);
    sb.append(" = ? ");
    sb.append(" GROUP BY ");
    sb.append(fieldList);

    return sb.toString();
  }

  static String insertSQL() {
    return String.format(
        "INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?)",
        TABLE_NAME,
        FIELD_FILE_ID,
        FIELD_ASSESSOR,
        FIELD_PERIOD_INDEX,
        FIELD_PERIOD_DATE,
        FIELD_SCORE_INDEX,
        FIELD_SCORE);
  }
}
