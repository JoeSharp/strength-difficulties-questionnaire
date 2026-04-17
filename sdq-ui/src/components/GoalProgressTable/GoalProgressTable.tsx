import React from "react";
import type { GoalProgress } from "@/api/GboApi/gboApi";

interface Props {
  goals: GoalProgress[];
}

const GoalProgressTable: React.FC<Props> = ({ goals }) => {
  return (
    <table>
      <thead>
        <tr>
          <th>Client ID</th>
          <th>Goal ID</th>
          <th>Description</th>
          <th>Assessor</th>
          <th>First Score</th>
          <th>Last Score</th>
        </tr>
      </thead>
      <tbody>
        {goals.map((goal) => (
          <tr key={goal.goal.goalId}>
            <td>{goal.goal.clientId}</td>
            <td>{goal.goal.goalId}</td>
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
