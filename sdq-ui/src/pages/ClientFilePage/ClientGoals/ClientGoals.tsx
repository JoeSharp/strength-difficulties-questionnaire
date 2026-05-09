import useGoalsForClient from "@/api/GboApi/useGoalsForClient";
import type { Assessor } from "@/api/types";
import GoalRow from "./GoalRow";

type Props = {
  clientId: string;
  assessor: Assessor;
};

const ClientGoals: React.FC<Props> = ({ clientId, assessor }) => {
  const { data: goals } = useGoalsForClient(clientId);
  return (
    <div>
      <h2>Goals</h2>

      <table>
        <thead>
          <tr>
            <th>Type</th>
            <th>Description</th>
            <th>First Score</th>
            <th>Last Score</th>
          </tr>
        </thead>
        <tbody>
          {goals ? (
            goals.map((goal) => (
              <GoalRow
                key={goal.goalId}
                goalId={goal.goalId}
                assessor={assessor}
              />
            ))
          ) : (
            <tr>
              <td colSpan={4}>No goals found.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default ClientGoals;
