import { useQuery } from "@tanstack/react-query";
import useInProgressContext from "@/context/InProgressContext";
import useAppNotificationContext from "@/context/AppNotificationContext";
import { BASE_GOAL_URL, type Goal } from "./gboApi";

function useGoalsForClient(clientId: string) {
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();

  return useQuery<void, Goal[]>({
    queryKey: ["goalsForClient", clientId],
    queryFn: async () => {
      const jobId = beginJob("Fetching goals");

      try {
        const response = await fetch(`${BASE_GOAL_URL}/forClient/${clientId}`);

        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to fetch goals: " + response.statusText,
          );
          throw new Error("Network response was not ok");
        }

        return await response.json(); // List<Goal>
      } finally {
        endJob(jobId);
      }
    },
    enabled: !!clientId, // don’t run until clientId is available
  });
}

export default useGoalsForClient;
