import React from "react";
import type { SdqSubmissionSummary } from "@/api/SdqApi/sdqApi";
import KeyedCountCell from "./KeyedCountCell";

interface Props {
  submissions: SdqSubmissionSummary[];
}

const SdqSummaryTable: React.FC<Props> = ({ submissions }) => {
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
        {submissions.map((submission, i) => (
          <tr key={i}>
            <td>{submission.clientId}</td>
            <td>{submission.period}</td>
            <td>
              <KeyedCountCell data={submission.categorySubTotals} />
            </td>
            <td>
              <KeyedCountCell data={submission.postureSubTotals} />
            </td>
            <td>{submission.totalDifficulties}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default SdqSummaryTable;
