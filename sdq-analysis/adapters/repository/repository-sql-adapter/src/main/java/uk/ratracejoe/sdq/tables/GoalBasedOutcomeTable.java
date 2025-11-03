package uk.ratracejoe.sdq.tables;

public interface GoalBasedOutcomeTable {
  String TABLE_NAME = "gbo";

  String FIELD_FILE_ID = "file_id";
  String FIELD_ASSESSOR = "assessor";
  String FIELD_PERIOD_INDEX = "period_index";
  String FIELD_PERIOD_DATE = "period_date";
  String FIELD_SCORE_INDEX = "score_index";
  String FIELD_SCORE = "score";

  static String getByFileSQL() {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT ");
    String fieldList =
        String.join(
            ",",
            FIELD_FILE_ID,
            FIELD_ASSESSOR,
            FIELD_PERIOD_INDEX,
            FIELD_PERIOD_DATE,
            FIELD_SCORE_INDEX,
            FIELD_SCORE);
    sb.append(fieldList);
    sb.append(" FROM ");
    sb.append(TABLE_NAME);
    sb.append(" WHERE ");
    sb.append(FIELD_FILE_ID);
    sb.append(" = ? ");

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
