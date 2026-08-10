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
        {Object.entries(summaries).map(([clientId, summariesForClient]) => {
          const clientRowSpan = Object.values(summariesForClient).reduce(
            (acc, curr) => acc + curr.length,
            0,
          );
          let isFirstClientRow = true;

          return Object.entries(summariesForClient).map(
            ([period, summariesForPeriod]) => {
              const periodRowSpan = summariesForPeriod.length;
              let isFirstPeriodRow = true;

              return summariesForPeriod.map((summary, summaryIndex) => (
                <tr key={`${clientId}-${period}-${summaryIndex}`}>
                  {isFirstClientRow && (
                    <td rowSpan={clientRowSpan}>
                      <ClientLink clientId={clientId} />
                    </td>
                  )}
                  {isFirstPeriodRow && (
                    <td key={`${clientId}-${period}`} rowSpan={periodRowSpan}>
                      {period}
                    </td>
                  )}
                  <td>{summary.assessor}</td>
                  <td>
                    <KeyedCountCell data={summary.categorySubTotals} />
                  </td>
                  <td>
                    <KeyedCountCell data={summary.postureSubTotals} />
                  </td>
                  <td>{summary.totalDifficulties}</td>

                  {/* flip flags at end of row */}
                  {(isFirstClientRow = false)}
                  {(isFirstPeriodRow = false)}
                </tr>
              ));
            },
          );
        })}
      </tbody>
    </table>
  );
};

export default SdqSummaryTable;
