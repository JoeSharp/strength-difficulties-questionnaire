import React from "react";
import useQueryGoalProgress from "@/api/GboApi/useQueryGoalProgress";
import GoalProgressTable from "@/components/GoalProgressTable";
import GoalQueryForm from "@/components/GoalQueryForm";
import type { GoalQueryDTO } from "@/api/GboApi/gboApi";
import { DEFAULT_ASSESSOR } from "@/api/types";

const DEFAULT_GOAL_QUERY: GoalQueryDTO = {
  goalTypes: [],
  assessors: [DEFAULT_ASSESSOR],
  filters: [],
  minProgress: 1,
  from: "2024-01-01",
  to: "2026-01-01",
};
function GoalBasedOutcomePage() {
  const queryGoalProgress = useQueryGoalProgress();
  const [goalQuery, setGoalQuery] =
    React.useState<GoalQueryDTO>(DEFAULT_GOAL_QUERY);

  const onClickQuery = () => {
    queryGoalProgress.mutate(goalQuery);
  };

  return (
    <div>
      <h2>Goal Based Outcomes</h2>
      <GoalQueryForm value={goalQuery} onChange={setGoalQuery} />
      <button onClick={onClickQuery}>Search</button>

      {queryGoalProgress.data && (
        <>
          <h3>{queryGoalProgress.data.length} matching goals found</h3>
          <GoalProgressTable goals={queryGoalProgress.data} />
        </>
      )}
    </div>
  );
}

export default GoalBasedOutcomePage;
