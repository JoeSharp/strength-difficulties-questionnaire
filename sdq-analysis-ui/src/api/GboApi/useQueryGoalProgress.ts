import { useMutation } from "@tanstack/react-query";
import useInProgressContext from "@/context/InProgressContext";
import useAppNotificationContext from "@/context/AppNotificationContext";
import { BASE_GOAL_URL, type GoalProgress, type GoalQueryDTO } from "./gboApi";

function useQueryGoalProgress() {
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();
  return useMutation<GoalProgress[], Error, GoalQueryDTO>({
    mutationFn: async (query) => {
      const jobId = beginJob("Querying goal progress");

      try {
        const response = await fetch(`${BASE_GOAL_URL}/query`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(query),
        });

        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to query goal progress: " + response.statusText,
          );
          throw new Error("Network response was not ok");
        }

        return await response.json(); // List<GoalProgress>
      } finally {
        endJob(jobId);
      }
    },
  });
}

export default useQueryGoalProgress;
