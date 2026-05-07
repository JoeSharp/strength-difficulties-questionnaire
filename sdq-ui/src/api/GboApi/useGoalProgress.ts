import { useQuery } from "@tanstack/react-query";
import useInProgressContext from "@/context/InProgressContext";
import useAppNotificationContext from "@/context/AppNotificationContext";
import { BASE_GOAL_URL, type GoalProgress } from "./gboApi";
import type { Assessor } from "@/api/types";

function useGoalProgress(goalId: string, assessor: Assessor) {
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();

  return useQuery<GoalProgress>({
    queryKey: ["goalProgress", goalId, assessor],
    enabled: !!goalId,
    queryFn: async () => {
      const jobId = beginJob("Fetching goal");

      try {
        const response = await fetch(
          `${BASE_GOAL_URL}/${goalId}/progress/${assessor}`,
        );

        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to fetch goal: " + response.statusText,
          );
          throw new Error("Network response was not ok");
        }

        return await response.json();
      } finally {
        endJob(jobId);
      }
    },
  });
}

export default useGoalProgress;
