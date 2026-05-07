import React from "react";
import type { SdqSubmissionSummary } from "@/api/SdqApi/sdqApi";
import KeyedCountCell from "./KeyedCountCell";

interface Props {
  summaries: SdqSubmissionSummary[];
}

const SdqSummaryTable: React.FC<Props> = ({ summaries }) => {
  return (
    <table>
      <thead>
        <tr>
          <th>Client ID</th>
          <th>Period</th>
          <th>Categories</th>
          <th>Postures</th>
          <th>Total Difficulties</th>
        </tr>
      </thead>
      <tbody>
        {summaries.map((summary, i) => (
          <tr key={i}>
            <td>{summary.clientId}</td>
            <td>{summary.period}</td>
            <td>
              <KeyedCountCell data={summary.categorySubTotals} />
            </td>
            <td>
              <KeyedCountCell data={summary.postureSubTotals} />
            </td>
            <td>{summary.totalDifficulties}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default SdqSummaryTable;
