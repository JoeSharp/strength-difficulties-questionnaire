import { useMutation } from "@tanstack/react-query";
import useInProgressContext from "@/context/InProgressContext";
import useAppNotificationContext from "@/context/AppNotificationContext";
import { BASE_GOAL_URL, type Goal } from "./gboApi";

function useCreateGoal() {
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();

  return useMutation<Goal, Error, Goal>({
    mutationFn: async (goal) => {
      const jobId = beginJob("Creating goal");

      try {
        const response = await fetch(BASE_GOAL_URL, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(goal),
        });

        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to create goal: " + response.statusText,
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

export default useCreateGoal;
