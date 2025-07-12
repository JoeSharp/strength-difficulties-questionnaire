package uk.ratracejoe.sdq_analysis.database.tables;

public interface GoalBasedOutcomeTable {
    String TABLE_NAME = "gbo";

    String FIELD_FILE_UUID = "fileUuid";
    String FIELD_ASSESSOR = "assessor";
    String FIELD_PERIOD_INDEX = "periodIndex";
    String FIELD_PERIOD_DATE = "periodDate";
    String FIELD_SCORE_INDEX = "scoreIndex";
    String FIELD_SCORE = "score";

    static String createTableSQL() {
        return String.format("CREATE TABLE IF NOT EXISTS %s (%s UUID, %s TEXT, %s INT, %s DATE, %s INT, %s INT)",
                TABLE_NAME,
                FIELD_FILE_UUID,
                FIELD_ASSESSOR,
                FIELD_PERIOD_INDEX,
                FIELD_PERIOD_DATE,
                FIELD_SCORE_INDEX,
                FIELD_SCORE);
    }

    static String getByFileSQL() {
        return String.format("SELECT * FROM %s WHERE %s = ?",
                TABLE_NAME,
                FIELD_FILE_UUID);
    }

    static String insertSQL() {
        return String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?)",
                TABLE_NAME,
                FIELD_FILE_UUID,
                FIELD_ASSESSOR,
                FIELD_PERIOD_INDEX,
                FIELD_PERIOD_DATE,
                FIELD_SCORE_INDEX,
                FIELD_SCORE);
    }
}
