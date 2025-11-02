package uk.ratracejoe.sdq.database.tables;

import java.util.Arrays;
import java.util.stream.Collectors;
import uk.ratracejoe.sdq.model.Category;
import uk.ratracejoe.sdq.model.Posture;

public interface SdqTable {
  String TABLE_NAME = "sdq";
  String FIELD_FILE_ID = "file_id";
  String FIELD_PERIOD_INDEX = "period";
  String FIELD_ASSESSOR = "assessor";
  String FIELD_STATEMENT = "statement";
  String FIELD_CATEGORY = "category";
  String FIELD_POSTURE = "posture";
  String FIELD_SCORE = "score";
  String FIELD_TOTAL = "total";

  static String insertSQL() {
    return String.format(
        "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?)",
        TABLE_NAME,
        FIELD_FILE_ID,
        FIELD_PERIOD_INDEX,
        FIELD_ASSESSOR,
        FIELD_STATEMENT,
        FIELD_CATEGORY,
        FIELD_POSTURE,
        FIELD_SCORE);
  }

  static String selectScoresPivotSQL() {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT ");
    String fieldList = String.join(",", FIELD_FILE_ID, FIELD_PERIOD_INDEX, FIELD_ASSESSOR);
    sb.append(fieldList);
    sb.append(",");
    String categoryStr =
        Arrays.stream(Category.values())
            .map(
                c ->
                    String.format(
                        "  SUM(CASE WHEN %s = '%s' THEN %s ELSE 0 END) AS %s",
                        FIELD_CATEGORY, c, FIELD_SCORE, c))
            .collect(Collectors.joining(","));
    sb.append(categoryStr);
    sb.append(",");
    String postureStr =
        Arrays.stream(Posture.values())
            .map(
                p ->
                    String.format(
                        "  SUM(CASE WHEN %s = '%s' THEN %s ELSE 0 END) AS %s",
                        FIELD_POSTURE, p, FIELD_SCORE, p))
            .collect(Collectors.joining(","));
    sb.append(postureStr);
    sb.append(String.format(", SUM(score) AS %s ", FIELD_TOTAL));
    sb.append(" FROM ");
    sb.append(TABLE_NAME);
    sb.append(" WHERE ");
    sb.append(FIELD_FILE_ID);
    sb.append(" = ? ");
    sb.append(" GROUP BY ");
    sb.append(fieldList);

    return sb.toString();
  }

  static String deleteAllSQL() {
    return String.format("DELETE FROM %s", TABLE_NAME);
  }
}
