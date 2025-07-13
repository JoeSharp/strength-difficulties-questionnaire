import React from "react";

import type { Assessor, GboSummary } from "../../api/types";
import Card from "../Card";

interface Props {
  assessor: Assessor;
  scores: GboSummary[];
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
              {Object.keys(scores[0].scores).map((k) => (
                <th key={k}>{k}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {scores.map((score) => (
              <tr key={score.uuid}>
                <td>{score.uuid}</td>
                <td>
                  {score.periodIndex} - {score.periodDate.toISOString()}
                </td>
                {Object.entries(score.scores).map(([i, s]) => (
                  <td key={i}>{s}</td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </Card>
    </div>
  );
}

export default GboTable;
