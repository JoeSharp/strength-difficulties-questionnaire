import { type Assessor, type SdqScore } from "../../api/types";
import Card from "../Card";

interface Props {
  assessor: Assessor;
  scores: SdqScore[];
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
              <th>Assessor</th>
              <th>Statement</th>
              <th>Score</th>
            </tr>
          </thead>
          <tbody>
            {scores.map((score) => (
              <tr key={score.fileId}>
                <td>{score.fileId}</td>
                <td>{score.period}</td>
                <td>{score.assessor}</td>
                <td>{score.statement}</td>
                <td>{score.score}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </Card>
    </div>
  );
}

export default SdqScoresTable;
