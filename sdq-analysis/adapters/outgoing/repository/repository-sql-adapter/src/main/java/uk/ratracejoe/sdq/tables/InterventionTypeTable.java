package uk.ratracejoe.sdq.tables;

public interface InterventionTypeTable {
  String TABLE_NAME = "intervention_type";
  String FIELD_CLIENT_ID = "client_id";
  String FIELD_INTERVENTION_TYPE = "intervention_type";

  static String getByFileSQL() {
    return String.format(
        "SELECT %s FROM %s WHERE %s = ?", FIELD_INTERVENTION_TYPE, TABLE_NAME, FIELD_CLIENT_ID);
  }

  static String insertOptionSQL() {
    return String.format(
        "INSERT INTO %s (%s, %s) VALUES (? ,?)",
        TABLE_NAME, FIELD_CLIENT_ID, FIELD_INTERVENTION_TYPE);
  }
}
