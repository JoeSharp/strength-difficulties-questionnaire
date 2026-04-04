import { type SdqScore } from "@/api/types";

interface Props {
  scores: SdqScore[];
}

function SdqScoresTable({ scores }: Props) {
  return (
    <div>
      <table className="table table-striped">
        <thead>
          <tr>
            <th>Client</th>
            <th>Assessor</th>
            <th>Period</th>
            <th>Assessor</th>
            <th>Statement</th>
            <th>Score</th>
          </tr>
        </thead>
        <tbody>
          {scores.map((score) => (
            <tr key={score.clientId}>
              <td>{score.clientId}</td>
              <td>{score.assessor}</td>
              <td>{score.period}</td>
              <td>{score.assessor}</td>
              <td>{score.statement}</td>
              <td>{score.score}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default SdqScoresTable;
