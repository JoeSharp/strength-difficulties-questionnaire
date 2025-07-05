package uk.ratracejoe.sdq_analysis.database.tables;

import uk.ratracejoe.sdq_analysis.dto.Category;
import uk.ratracejoe.sdq_analysis.dto.Posture;

import java.util.Arrays;
import java.util.stream.Collectors;

public interface SdqResponseTable {
    String TABLE_NAME = "sdq";
    String FIELD_FILE_UUID = "uuid";
    String FIELD_PERIOD_INDEX = "period";
    String FIELD_ASSESSOR = "assessor";
    String FIELD_STATEMENT = "statement";
    String FIELD_CATEGORY = "category";
    String FIELD_POSTURE = "posture";
    String FIELD_SCORE = "score";
    String FIELD_TOTAL = "total";

    static String createTableSQL() {
        return String.format("CREATE TABLE IF NOT EXISTS %s (%s UUID, %s INT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s INT)",
                TABLE_NAME,
                FIELD_FILE_UUID,
                FIELD_PERIOD_INDEX,
                FIELD_ASSESSOR,
                FIELD_STATEMENT,
                FIELD_CATEGORY,
                FIELD_POSTURE,
                FIELD_SCORE);
    }

    static String insertSQL() {
        return String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?)",
                TABLE_NAME,
                FIELD_FILE_UUID,
                FIELD_PERIOD_INDEX,
                FIELD_ASSESSOR,
                FIELD_STATEMENT,
                FIELD_CATEGORY,
                FIELD_POSTURE,
                FIELD_SCORE);
    }

    static String selectScoresSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        String fieldList = String.join(",", FIELD_FILE_UUID, FIELD_PERIOD_INDEX, FIELD_ASSESSOR);
        sb.append(fieldList);
        sb.append(",");
        String categoryStr = Arrays.stream(Category.values())
                .map(c -> String.format("  SUM(CASE WHEN category = '%s' THEN score ELSE 0 END) AS %s", c, c))
                .collect(Collectors.joining(","));
        sb.append(categoryStr);
        sb.append(",");
        String postureStr = Arrays.stream(Posture.values())
                .map(p -> String.format("  SUM(CASE WHEN posture = '%s' THEN score ELSE 0 END) AS %s", p, p))
                .collect(Collectors.joining(","));
        sb.append(postureStr);
        sb.append(String.format(", SUM(score) AS %s ", FIELD_TOTAL));
        sb.append(" FROM ");
        sb.append(TABLE_NAME);
        sb.append(" WHERE ");
        sb.append(FIELD_FILE_UUID);
        sb.append(" = ? ");
        sb.append(" GROUP BY ");
        sb.append(fieldList);

        return sb.toString();
    }

    static String deleteAllSQL() {
        return String.format("DELETE FROM %s", TABLE_NAME);
    }
}
