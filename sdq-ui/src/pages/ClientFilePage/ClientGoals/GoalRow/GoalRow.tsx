import type { Assessor } from "@/api/types";
import useGoalProgress from "@/api/GboApi/useGoalProgress";
import useReference from "@/api/ReferenceApi/useReference";

type Props = {
  goalId: string;
  assessor: Assessor;
};

const GoalRow: React.FC<Props> = ({ goalId, assessor }) => {
  const { getLabelForGoalType } = useReference();
  const { data: goalProgress } = useGoalProgress(goalId, assessor);
  if (!goalProgress) return null;

  return (
    <tr>
      <td>{getLabelForGoalType(goalProgress.goal.type)}</td>
      <td>{goalProgress.goal.description}</td>
      <td>First Score: {goalProgress?.firstScore}</td>
      <td>Last Score: {goalProgress?.lastScore}</td>
    </tr>
  );
};
export default GoalRow;
