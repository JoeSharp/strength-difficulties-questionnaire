import React from "react";
import type { SdqSubmissionSummary } from "@/api/SdqApi/sdqApi";

interface Props {
  submissions: SdqSubmissionSummary[];
}

const SdqSummaryTable: React.FC<Props> = ({ submissions }) => {
  return (
    <table>
      <thead>
        <tr>
          <th>Categories</th>
          <th>Postures</th>
          <th>Total Difficulties</th>
        </tr>
      </thead>
      <tbody>
        {submissions.map((submission, i) => (
          <tr key={i}>
            <td>{JSON.stringify(submission.categorySubTotals)}</td>
            <td>{JSON.stringify(submission.postureSubTotals)}</td>
            <td>{submission.totalDifficulties}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default SdqSummaryTable;
