package uk.ratracejoe.sdq_analysis.repository;

public interface SdqResponseTable {
    String TABLE_NAME = "sdq";
    String FIELD_FILE_UUID = "uuid";
    String FIELD_PERIOD_INDEX = "period";
    String FIELD_ASSESSOR = "assessor";
    String FIELD_STATEMENT = "statement";
    String FIELD_SCORE = "score";

    static String createTableSQL() {
        return String.format("CREATE TABLE IF NOT EXISTS %s (%s UUID, %s INT, %s TEXT, %s TEXT, %s TEXT)",
                TABLE_NAME,
                FIELD_FILE_UUID,
                FIELD_PERIOD_INDEX,
                FIELD_ASSESSOR,
                FIELD_STATEMENT,
                FIELD_SCORE);
    }

    static String insertSQL() {
        return String.format("INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?)",
                TABLE_NAME,
                FIELD_FILE_UUID,
                FIELD_PERIOD_INDEX,
                FIELD_ASSESSOR,
                FIELD_STATEMENT,
                FIELD_SCORE);
    }

    static String selectByFileUuid() {
        return String.format("SELECT * FROM %s WHERE %s=?",
                TABLE_NAME, FIELD_FILE_UUID);
    }

    static String selectAllSQL() {
        return String.format("SELECT * FROM %s", TABLE_NAME);
    }
}
