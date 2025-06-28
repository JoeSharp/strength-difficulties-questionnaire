package uk.ratracejoe.sdq_analysis.repository;

public interface UploadFileTable {
    String TABLE_NAME = "upload_file";
    String FIELD_UUID = "uuid";
    String FIELD_FILENAME = "filename";

    static String createTableSQL() {
        return String.format("CREATE TABLE IF NOT EXISTS %s (%s UUID PRIMARY KEY, %s TEXT)",
                TABLE_NAME, FIELD_UUID, FIELD_FILENAME);
    }

    static String insertSQL() {
        return String.format("INSERT INTO %s (%s, %s) VALUES (?, ?)", TABLE_NAME, FIELD_UUID, FIELD_FILENAME);
    }

    static String selectByUUID() {
        return String.format("SELECT %s, %s FROM %s WHERE %s=?",
                FIELD_UUID, FIELD_FILENAME, TABLE_NAME, FIELD_UUID);
    }

    static String selectAllSQL() {
        return String.format("SELECT %s, %s FROM %s",
                FIELD_UUID, FIELD_FILENAME, TABLE_NAME);
    }
}
