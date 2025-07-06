package uk.ratracejoe.sdq_analysis.database.tables;

import java.util.List;
import java.util.stream.Collectors;

public interface ClientFileTable {
    String TABLE_NAME = "client_file";
    String FIELD_UUID = "uuid";
    String FIELD_FILENAME = "filename";
    String FIELD_DOB = "dateOfBirth";
    String FIELD_GENDER = "gender";
    String FIELD_COUNCIL = "council";
    String FIELD_ETHNICITY = "ethnicity";
    String FIELD_EAL = "eal";
    String FIELD_DISABILITY_STATUS = "disabilityStatus";
    String FIELD_DISABILITY_TYPE = "disabilityType";
    String FIELD_CARE_EXPERIENCE = "careExperience";
    String FIELD_ACES = "aces";
    String FIELD_FUNDING_SOURCE = "fundingSource";
    List<String> FIELDS = List.of(
            FIELD_UUID,
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
            FIELD_FUNDING_SOURCE
    );

    static String createTableSQL() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                String.format("%s UUID PRIMARY KEY, ", FIELD_UUID) +
                String.format("%s TEXT, ", FIELD_FILENAME) +
                String.format("%s DATE, ", FIELD_DOB) +
                String.format("%s TEXT, ", FIELD_GENDER) +
                String.format("%s TEXT, ", FIELD_COUNCIL) +
                String.format("%s TEXT, ", FIELD_ETHNICITY) +
                String.format("%s TEXT, ", FIELD_EAL) +
                String.format("%s TEXT, ", FIELD_DISABILITY_STATUS) +
                String.format("%s TEXT, ", FIELD_DISABILITY_TYPE) +
                String.format("%s TEXT, ", FIELD_CARE_EXPERIENCE) +
                String.format("%s INT, ", FIELD_ACES) +
                String.format("%s TEXT ", FIELD_FUNDING_SOURCE) +
                ")";
    }

    static String insertSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO " + TABLE_NAME + " (");
        String fieldNames = String.join(",", FIELDS);
        sb.append(fieldNames);
        sb.append(") VALUES (");
        String placeholders = FIELDS.stream()
                .map(f -> "?")
                .collect(Collectors.joining(", "));
        sb.append(placeholders);
        sb.append(")");
        return sb.toString();
    }

    static String selectByUUID() {
        return String.format("SELECT * FROM %s WHERE %s=?",
                TABLE_NAME, FIELD_UUID);
    }

    static String selectAllSQL() {
        return String.format("SELECT * FROM %s", TABLE_NAME);
    }

    static String deleteAllSQL() {
        return String.format("DELETE FROM %s", TABLE_NAME);
    }
}
