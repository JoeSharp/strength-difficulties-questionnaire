package uk.ratracejoe.sdq_analysis.database.tables;

public interface InterventionTypeTable {
    String TABLE_NAME = "intervention_type";
    String FIELD_FILE_UUID = "file_uuid";
    String FIELD_INTERVENTION_TYPE = "intervention_type";

    static String createTableSQL() {
        return String.format("CREATE TABLE IF NOT EXISTS %s (%s UUID, %s TEXT)",
                TABLE_NAME,
                FIELD_FILE_UUID,
                FIELD_INTERVENTION_TYPE);
    }

    static String getByFileSQL() {
        return String.format("SELECT %s FROM %s WHERE %s = ?",
                FIELD_INTERVENTION_TYPE,
                TABLE_NAME,
                FIELD_FILE_UUID);
    }

    static String insertOptionSQL() {
        return String.format("INSERT INTO %s (%s, %s) VALUES (? ,?)",
                TABLE_NAME,
                FIELD_FILE_UUID,
                FIELD_INTERVENTION_TYPE);
    }
}
