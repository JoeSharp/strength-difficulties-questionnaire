import type { Assessor, GboScore } from "../../api/types";
import Card from "../Card";

interface Props {
  assessor: Assessor;
  scores: GboScore[];
}

function GboTable({ assessor, scores }: Props) {
  if (scores.length === 0) return <div>No GBO</div>;

  return (
    <div>
      <Card id={`gbo-${assessor}`} title={`GBO Scores for ${assessor}`}>
        <table className="table table-striped">
          <thead>
            <tr>
              <th>File</th>
              <th>Period</th>
              <th>Goal</th>
              <th>Score</th>
            </tr>
          </thead>
          <tbody>
            {scores.map((score) => (
              <tr key={score.fileId}>
                <td>{score.fileId}</td>
                <td>
                  {score.periodIndex} - {score.periodDate.toISOString()}
                </td>
                <th>{score.scoreIndex}</th>
                <th>{score.score}</th>
              </tr>
            ))}
          </tbody>
        </table>
      </Card>
    </div>
  );
}

export default GboTable;
