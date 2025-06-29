package uk.ratracejoe.sdq_analysis.repository;

import uk.ratracejoe.sdq_analysis.dto.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface UploadFileTable {
    String TABLE_NAME = "upload_file";
    String FIELD_UUID = "uuid";
    String FIELD_FILENAME = "filename";
    String FIELD_DOB = "dateOfBirth";
    String FIELD_GENDER = "gender";
    String FIELD_ETHNICITY = "ethnicity";
    String FIELD_EAL = "eal";
    String FIELD_DISABILITY_STATUS = "disabilityStatus";
    String FIELD_DISABILITY_TYPE = "disabilityType";
    String FIELD_CARE_EXPERIENCE = "careExperience";
    String FIELD_INTERVENTION_TYPE = "interventionType";
    String FIELD_ACES = "aces";
    String FIELD_FUNDING_SOURCE = "fundingSource";
    List<String> FIELDS = List.of(
            FIELD_FILENAME,
            FIELD_DOB,
            FIELD_GENDER,
            FIELD_ETHNICITY,
            FIELD_EAL,
            FIELD_DISABILITY_STATUS,
            FIELD_DISABILITY_TYPE,
            FIELD_CARE_EXPERIENCE,
            FIELD_INTERVENTION_TYPE,
            FIELD_ACES,
            FIELD_FUNDING_SOURCE
    );

    static String createTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(");
        sb.append(String.format("%s UUID PRIMARY KEY, ", FIELD_UUID));
        String fieldsStr = FIELDS.stream()
                .map(f -> String.format("%s TEXT", f))
                .collect(Collectors.joining(","));
        sb.append(fieldsStr);
        sb.append(")");

        return sb.toString();
    }

    static String insertSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO " + TABLE_NAME);
        String fieldNames = String.join(",", FIELDS);
        sb.append(String.format("(%s, %s)", FIELD_UUID, fieldNames));
        sb.append(" VALUES ");
        String placeholders = FIELDS.stream()
                .map(f -> "?")
                .collect(Collectors.joining(", "));
        sb.append(String.format("(?, %s)", placeholders));
        return sb.toString();
    }

    static String selectByUUID() {
        return String.format("SELECT * FROM %s WHERE %s=?",
                TABLE_NAME, FIELD_UUID);
    }

    static String selectAllSQL() {
        return String.format("SELECT * FROM %s", TABLE_NAME);
    }
}
