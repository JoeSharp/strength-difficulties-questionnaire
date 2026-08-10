import { useMutation } from "@tanstack/react-query";
import useInProgressContext from "@/context/InProgressContext";
import useAppNotificationContext from "@/context/AppNotificationContext";
import { BASE_GOAL_URL, type Goal } from "./gboApi";

function useUpdateGoal() {
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();

  return useMutation<Goal, Error, Goal>({
    mutationFn: async (goal) => {
      const jobId = beginJob("Updating goal");

      try {
        const response = await fetch(BASE_GOAL_URL, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(goal),
        });

        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to update goal: " + response.statusText,
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

export default useUpdateGoal;
