package uk.ratracejoe.sdq.tables;

public interface SdqTable {
  String TABLE_NAME = "sdq";
  String FIELD_FILE_ID = "file_id";
  String FIELD_PERIOD_INDEX = "period";
  String FIELD_ASSESSOR = "assessor";
  String FIELD_STATEMENT = "statement";
  String FIELD_CATEGORY = "category";
  String FIELD_POSTURE = "posture";
  String FIELD_SCORE = "score";

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

  static String selectScoresSQL() {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT ");
    String fieldList =
        String.join(
            ",", FIELD_FILE_ID, FIELD_PERIOD_INDEX, FIELD_ASSESSOR, FIELD_STATEMENT, FIELD_SCORE);
    sb.append(fieldList);
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
