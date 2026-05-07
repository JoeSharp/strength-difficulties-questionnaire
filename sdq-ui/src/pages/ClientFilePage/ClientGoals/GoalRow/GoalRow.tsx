import type { Assessor } from "@/api/types";
import useGoalProgress from "../../../../api/GboApi/useGoalProgress";

type Props = {
  goalId: string;
  assessor: Assessor;
};

const GoalRow: React.FC<Props> = ({ goalId, assessor }) => {
  const { data: goalProgress } = useGoalProgress(goalId, assessor);
  if (!goalProgress) return null;

  return (
    <tr>
      <td>{goalProgress.goal.description}</td>
      <td>First Score: {goalProgress?.firstScore}</td>
      <td>Last Score: {goalProgress?.lastScore}</td>
    </tr>
  );
};
export default GoalRow;
