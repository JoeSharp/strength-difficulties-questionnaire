package uk.ratracejoe.sdq.database.tables;

public interface DemographicOptionTable {
  String TABLE_NAME = "demographic_option";
  String FIELD_DEMOGRAPHIC = "demographic";
  String FIELD_OPTION_TEXT = "option_text";

  static String getAll() {
    return String.format("SELECT %s, %s FROM %s", FIELD_DEMOGRAPHIC, FIELD_OPTION_TEXT, TABLE_NAME);
  }

  static String getOptionsSQL() {
    return String.format(
        "SELECT %s FROM %s WHERE %s = ?", FIELD_OPTION_TEXT, TABLE_NAME, FIELD_DEMOGRAPHIC);
  }

  static String insertOptionSQL() {
    return String.format(
        "INSERT INTO %s (%s, %s) VALUES (? ,?)", TABLE_NAME, FIELD_DEMOGRAPHIC, FIELD_OPTION_TEXT);
  }
}
