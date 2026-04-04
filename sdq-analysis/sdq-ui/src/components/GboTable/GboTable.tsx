import type { GboScore } from "@/api/types";
interface Props {
  scores: GboScore[];
}

function GboTable({ scores }: Props) {
  if (scores.length === 0) return <div>No GBO</div>;

  return (
    <div>
      <table className="table table-striped">
        <thead>
          <tr>
            <th>Client</th>
            <th>Assessor</th>
            <th>Period</th>
            <th>Goal</th>
            <th>Score</th>
          </tr>
        </thead>
        <tbody>
          {scores.map((score) => (
            <tr key={score.clientId}>
              <td>{score.clientId}</td>
              <td>{score.assessor}</td>
              <td>
                {score.periodIndex} - {score.periodDate.toISOString()}
              </td>
              <th>{score.scoreIndex}</th>
              <th>{score.score}</th>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default GboTable;
