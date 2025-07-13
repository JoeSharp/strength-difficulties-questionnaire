package uk.ratracejoe.sdq_analysis.database.tables;

import uk.ratracejoe.sdq_analysis.dto.Category;
import uk.ratracejoe.sdq_analysis.dto.Posture;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static uk.ratracejoe.sdq_analysis.service.xslx.XslxGboExtractor.NUMBER_SCORES_EXPECTED;

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

    static String pivotFieldForScore(int scoreIndex) {
        return String.format("period_%d", scoreIndex);
    }

    static String selectScoresPivotSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        String fieldList = String.join(",", FIELD_FILE_UUID, FIELD_ASSESSOR, FIELD_PERIOD_INDEX, FIELD_PERIOD_DATE);
        sb.append(fieldList);
        sb.append(",");
        String scoresStr = IntStream.range(1, NUMBER_SCORES_EXPECTED + 1)
                .mapToObj(i -> String.format("  SUM(CASE WHEN %s = '%s' THEN %s ELSE 0 END) AS %s", FIELD_SCORE_INDEX, i, FIELD_SCORE, pivotFieldForScore(i)))
                .collect(Collectors.joining(","));
        sb.append(scoresStr);
        sb.append(" FROM ");
        sb.append(TABLE_NAME);
        sb.append(" WHERE ");
        sb.append(FIELD_FILE_UUID);
        sb.append(" = ? ");
        sb.append(" GROUP BY ");
        sb.append(fieldList);

        return sb.toString();
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
