package uk.ratracejoe.sdq.database.tables;

import java.util.List;
import java.util.stream.Collectors;

public interface ClientFileTable {
  String TABLE_NAME = "client_file";
  String FIELD_FILE_ID = "file_id";
  String FIELD_FILENAME = "filename";
  String FIELD_DOB = "date_of_birth";
  String FIELD_GENDER = "gender";
  String FIELD_COUNCIL = "council";
  String FIELD_ETHNICITY = "ethnicity";
  String FIELD_EAL = "eal";
  String FIELD_DISABILITY_STATUS = "disability_status";
  String FIELD_DISABILITY_TYPE = "disability_type";
  String FIELD_CARE_EXPERIENCE = "care_experience";
  String FIELD_ACES = "aces";
  String FIELD_FUNDING_SOURCE = "funding_source";
  List<String> FIELDS =
      List.of(
          FIELD_FILE_ID,
          FIELD_FILENAME,
          FIELD_DOB,
          FIELD_GENDER,
          FIELD_COUNCIL,
          FIELD_ETHNICITY,
          FIELD_EAL,
          FIELD_DISABILITY_STATUS,
          FIELD_DISABILITY_TYPE,
          FIELD_CARE_EXPERIENCE,
          FIELD_ACES,
          FIELD_FUNDING_SOURCE);

  static String insertSQL() {
    StringBuilder sb = new StringBuilder();
    sb.append("INSERT INTO " + TABLE_NAME + " (");
    String fieldNames = String.join(",", FIELDS);
    sb.append(fieldNames);
    sb.append(") VALUES (");
    String placeholders = FIELDS.stream().map(f -> "?").collect(Collectors.joining(", "));
    sb.append(placeholders);
    sb.append(")");
    return sb.toString();
  }

  static String selectByUUID() {
    return String.format("SELECT * FROM %s WHERE %s=?", TABLE_NAME, FIELD_FILE_ID);
  }

  static String selectAllSQL() {
    return String.format("SELECT * FROM %s", TABLE_NAME);
  }

  static String deleteAllSQL() {
    return String.format("DELETE FROM %s", TABLE_NAME);
  }
}
