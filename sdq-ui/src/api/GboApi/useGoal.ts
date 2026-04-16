import { useQuery } from "@tanstack/react-query";
import useInProgressContext from "@/context/InProgressContext";
import useAppNotificationContext from "@/context/AppNotificationContext";
import { BASE_GOAL_URL, type Goal } from "./gboApi";

function useGoal(goalId: string) {
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();

  return useQuery<void, Goal>({
    queryKey: ["goal", goalId],
    enabled: !!goalId,
    queryFn: async () => {
      const jobId = beginJob("Fetching goal");

      try {
        const response = await fetch(`${BASE_GOAL_URL}/${goalId}`);

        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to fetch goal: " + response.statusText,
          );
          throw new Error("Network response was not ok");
        }

        return await response.json(); // Goal
      } finally {
        endJob(jobId);
      }
    },
  });
}

export default useGoal;
