import React from "react";
import type { GoalProgress } from "@/api/GboApi/gboApi";
import useReference from "@/api/ReferenceApi/useReference";
import ClientLink from "@/components/ClientLink";

interface Props {
  goals: GoalProgress[];
}

const GoalProgressTable: React.FC<Props> = ({ goals }) => {
  const { getLabelForGoalType } = useReference();
  return (
    <table>
      <thead>
        <tr>
          <th>Client ID</th>
          <th>Type</th>
          <th>Description</th>
          <th>Assessor</th>
          <th>First Score</th>
          <th>Last Score</th>
        </tr>
      </thead>
      <tbody>
        {goals.map((goal) => (
          <tr key={goal.goal.goalId}>
            <td>
              <ClientLink clientId={goal.goal.clientId} />
            </td>
            <td>{getLabelForGoalType(goal.goal.type)}</td>
            <td>{goal.goal.description}</td>
            <td>{goal.assessor}</td>
            <td>{goal.firstScore}</td>
            <td>{goal.lastScore}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default GoalProgressTable;
