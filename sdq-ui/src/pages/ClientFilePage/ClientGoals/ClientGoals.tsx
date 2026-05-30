import useGoalsProgressForClient from "@/api/GboApi/useGoalsProgressForClient";
import type { Assessor } from "@/api/types";
import GoalProgressTable from "@/components/GoalProgressTable";

type Props = {
  clientId: string;
  assessor: Assessor;
};

const ClientGoals: React.FC<Props> = ({ clientId, assessor }) => {
  const { data: goals } = useGoalsProgressForClient(clientId, assessor);
  return (
    <div>
      <h2>Goals</h2>
      <GoalProgressTable goals={goals} />
    </div>
  );
};

export default ClientGoals;
