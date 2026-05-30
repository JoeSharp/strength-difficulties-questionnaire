import React from "react";
import type { SdqProgressSummaryByClient } from "@/api/SdqApi/sdqApi";
import KeyedProgressCell, { ProgressCell } from "./KeyedProgressCell";

interface Props {
  summaries: SdqProgressSummaryByClient;
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
        {Object.entries(summaries).map(([clientId, summariesForClient], i) => {
          const rowSpan = summariesForClient.length;

          return summariesForClient.map((summary, index) => (
            <tr key={i}>
              {index === 0 && <td rowSpan={rowSpan}>{clientId}</td>}
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
          ));
        })}
      </tbody>
    </table>
  );
};

export default SdqProgressTable;
