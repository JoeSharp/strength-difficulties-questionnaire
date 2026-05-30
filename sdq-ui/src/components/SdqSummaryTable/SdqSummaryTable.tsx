import React from "react";
import type { SdqSubmissionSummaryByClient } from "@/api/SdqApi/sdqApi";
import KeyedCountCell from "./KeyedCountCell";
import ClientLink from "../ClientLink";

interface Props {
  summaries: SdqSubmissionSummaryByClient;
}

const SdqSummaryTable: React.FC<Props> = ({ summaries }) => {
  return (
    <table>
      <thead>
        <tr>
          <th>Client ID</th>
          <th>Period</th>
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
            <tr key={`${clientId}-${index}`}>
              {index === 0 && (
                <td rowSpan={rowSpan}>
                  <ClientLink clientId={summary.clientId} />
                </td>
              )}
              <td>{summary.period}</td>
              <td>{summary.assessor}</td>
              <td>
                <KeyedCountCell data={summary.categorySubTotals} />
              </td>
              <td>
                <KeyedCountCell data={summary.postureSubTotals} />
              </td>
              <td>{summary.totalDifficulties}</td>
            </tr>
          ));
        })}
      </tbody>
    </table>
  );
};

export default SdqSummaryTable;
