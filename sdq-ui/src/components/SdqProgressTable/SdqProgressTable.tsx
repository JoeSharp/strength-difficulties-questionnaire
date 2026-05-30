import React from "react";
import type { SdqProgressSummary } from "@/api/SdqApi/sdqApi";
import KeyedProgressCell, { ProgressCell } from "./KeyedProgressCell";

interface Props {
  summaries: SdqProgressSummary[];
}

const SdqProgressTable: React.FC<Props> = ({ summaries }) => {
  return (
    <table>
      <thead>
        <tr>
          <th>Client ID</th>
          <th>Assessor</th>
          <th>Categories</th>
          <th>Postures</th>
          <th>Total Difficulties</th>
        </tr>
      </thead>
      <tbody>
        {summaries.map((summary, i) => (
          <tr key={i}>
            <td>{summary.clientId}</td>
            <td>{summary.assessor}</td>
            <td>
              <KeyedProgressCell data={summary.categoryProgress} />
            </td>
            <td>
              <KeyedProgressCell data={summary.postureProgress} />
            </td>
            <td>
              <ProgressCell progress={summary.totalDifficulties} />
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default SdqProgressTable;
