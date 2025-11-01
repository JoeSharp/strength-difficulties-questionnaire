import {
  CATEGORIES,
  POSTURES,
  type Assessor,
  type SdqSummary,
} from "../../api/types";
import Card from "../Card";

interface Props {
  assessor: Assessor;
  scores: SdqSummary[];
}

function SdqScoresTable({ assessor, scores }: Props) {
  return (
    <div>
      <Card id={`sdq-${assessor}`} title={`SDQ Scores for ${assessor}`}>
        <table className="table table-striped">
          <thead>
            <tr>
              <th>File</th>
              <th>Period</th>
              {CATEGORIES.map((c) => (
                <th key={c}>{c}</th>
              ))}
              {POSTURES.map((p) => (
                <th key={p}>{p}</th>
              ))}
              <th>Total Difficulties</th>
            </tr>
          </thead>
          <tbody>
            {scores.map((score) => (
              <tr key={score.uuid}>
                <td>{score.uuid}</td>
                <td>{score.period}</td>
                {CATEGORIES.map((c) => (
                  <td key={c}>{score.categoryScores[c]}</td>
                ))}
                {POSTURES.map((p) => (
                  <td key={p}>{score.postureScores[p]}</td>
                ))}
                <td>{score.total}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </Card>
    </div>
  );
}

export default SdqScoresTable;
